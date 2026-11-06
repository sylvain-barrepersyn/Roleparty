package app.roleparty.core.rabbitmq

import com.rabbitmq.client.ConnectionFactory
import io.ktor.server.testing.testApplication
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testcontainers.rabbitmq.RabbitMQContainer

class RabbitMqTest {

    @Serializable
    data class Delivered(val content: String) : RabbitMqEvent {
        companion object {
            const val EXCHANGE = "test.delivered"
        }
    }

    class DeliveredHandler(private val received: CompletableDeferred<Pair<Uuid, Delivered>>) : RabbitMqEventHandler<Delivered> {
        override suspend fun handle(eventId: Uuid, event: Delivered) {
            received.complete(eventId to event)
        }

        companion object {
            const val QUEUE = "test.delivered.queue"
        }
    }

    @Serializable
    data class Rejected(val content: String) : RabbitMqEvent {
        companion object {
            const val EXCHANGE = "test.rejected"
        }
    }

    class RejectedHandler : RabbitMqEventHandler<Rejected> {
        override suspend fun handle(eventId: Uuid, event: Rejected) {
            error("Impossible to handle this event")
        }

        companion object {
            const val QUEUE = "test.rejected.queue"
        }
    }

    @Test
    fun `should deliver a published event to its handler`() = testApplication {
        // Given
        val rabbitMq = application.loadRabbitMq(container.env())
        val received = CompletableDeferred<Pair<Uuid, Delivered>>()
        rabbitMq.registerEventHandler(DeliveredHandler(received))
        val id = Uuid.generateV7()

        // When
        rabbitMq.publish(Delivered("hello").toRabbitMqMessage(id))

        // Then
        assertEquals(id to Delivered("hello"), withContext(Dispatchers.IO) { withTimeout(10.seconds) { received.await() } })
    }

    @Test
    fun `should publish to an exchange without any queue`() = testApplication {
        // Given
        val rabbitMq = application.loadRabbitMq(container.env())

        // When / Then
        rabbitMq.publish(RabbitMqMessage(Uuid.generateV7(), "test.unbound", "type", "{}"))
    }

    @Test
    fun `should send an event to the dead-letter queue when its handler fails`() = testApplication {
        // Given
        val rabbitMq = application.loadRabbitMq(container.env())
        rabbitMq.registerEventHandler(RejectedHandler())
        val id = Uuid.generateV7()

        // When
        rabbitMq.publish(Rejected("hello").toRabbitMqMessage(id))

        // Then
        assertEquals(id.toString(), withContext(Dispatchers.IO) { deadLetterMessageId("${RejectedHandler.QUEUE}.dead-letter") })
    }

    private suspend fun deadLetterMessageId(queue: String): String =
        ConnectionFactory().apply {
            host = container.host
            port = container.amqpPort
            username = container.adminUsername
            password = container.adminPassword
        }.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                withTimeout(10.seconds) {
                    var response = channel.basicGet(queue, true)
                    while (response == null) {
                        delay(100)
                        response = channel.basicGet(queue, true)
                    }
                    response.props.messageId
                }
            }
        }

    private fun RabbitMQContainer.env() = mapOf(
        "RABBITMQ_HOST" to host,
        "RABBITMQ_PORT" to amqpPort.toString(),
        "RABBITMQ_USER" to adminUsername,
        "RABBITMQ_PASSWORD" to adminPassword,
    )

    private companion object {
        val container = RabbitMQContainer("rabbitmq:4.2.3-management-alpine").apply { start() }
    }
}
