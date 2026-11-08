package app.roleparty.core.rabbitmq

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.slf4j.Logger

private const val ROUTING_KEY = ""
private const val CONTENT_TYPE = "application/json"
private const val PERSISTENT = 2
private const val CONFIRM_TIMEOUT_MILLISECONDS = 5_000L
private const val PREFETCH = 1
private const val DEAD_LETTER_ARGUMENT = "x-dead-letter-exchange"
private const val DEAD_LETTER_SUFFIX = ".dead-letter"

class RabbitMq internal constructor(
    private val connection: Connection,
    private val log: Logger,
) {
    private val publication = Mutex()
    private var publicationChannel: Channel? = null
    private val declaredExchanges = mutableSetOf<String>()
    private val deadLetterQueues = ConcurrentHashMap.newKeySet<String>()

    suspend fun publish(message: RabbitMqMessage): Unit = publication.withLock {
        withContext(Dispatchers.IO) {
            val channel = openPublicationChannel()
            if (declaredExchanges.add(message.exchange)) channel.declareEventExchange(message.exchange)

            channel.basicPublish(message.exchange, ROUTING_KEY, message.properties(), message.body.toByteArray())
            channel.waitForConfirmsOrDie(CONFIRM_TIMEOUT_MILLISECONDS)
        }
    }

    suspend fun deadLetterCounts(): Map<String, Long> = withContext(Dispatchers.IO) {
        connection.createChannel().use { channel ->
            deadLetterQueues.associateWith { channel.queueDeclarePassive(it).messageCount.toLong() }
        }
    }

    inline fun <reified E : RabbitMqEvent> registerEventHandler(handler: RabbitMqEventHandler<E>) {
        consume(
            exchange = E::class.declaredConstant("EXCHANGE"),
            queue = handler::class.declaredConstant("QUEUE"),
        ) { eventId, body ->
            handler.handle(eventId, rabbitMqJson.decodeFromString<E>(body))
        }
    }

    @PublishedApi
    internal fun consume(exchange: String, queue: String, handle: suspend (Uuid, String) -> Unit) {
        val channel = connection.createChannel()
        channel.declareEventExchange(exchange)
        channel.declareQueueWithDeadLetter(queue)
        channel.queueBind(queue, exchange, ROUTING_KEY)
        channel.basicQos(PREFETCH)

        // The delivery thread is blocked by the handler, which keeps events in order and the channel on a single thread
        val onDelivery = DeliverCallback { _, delivery ->
            val tag = delivery.envelope.deliveryTag
            try {
                runBlocking { handle(Uuid.parse(delivery.properties.messageId), delivery.bodyAsString()) }
                channel.basicAck(tag, false)
            } catch (exception: Exception) {
                log.error("Impossible to handle a message from queue $queue, sending it to its dead-letter queue", exception)
                channel.basicNack(tag, false, false)
            }
        }
        channel.basicConsume(queue, false, onDelivery, CancelCallback { })
    }

    private fun openPublicationChannel(): Channel =
        publicationChannel?.takeIf { it.isOpen }
            ?: connection.createChannel().also {
                it.confirmSelect()
                publicationChannel = it
                declaredExchanges.clear()
            }

    private fun Channel.declareEventExchange(exchange: String) {
        exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true)
    }

    private fun Channel.declareQueueWithDeadLetter(queue: String) {
        val deadLetter = "$queue$DEAD_LETTER_SUFFIX"
        deadLetterQueues += deadLetter
        declareEventExchange(deadLetter)
        queueDeclare(deadLetter, true, false, false, null)
        queueBind(deadLetter, deadLetter, ROUTING_KEY)
        queueDeclare(queue, true, false, false, mapOf(DEAD_LETTER_ARGUMENT to deadLetter))
    }

    private fun RabbitMqMessage.properties(): AMQP.BasicProperties =
        AMQP.BasicProperties.Builder()
            .messageId(id.toString())
            .type(type)
            .contentType(CONTENT_TYPE)
            .deliveryMode(PERSISTENT)
            .build()

    private fun Delivery.bodyAsString() = String(body)
}
