package app.roleparty.core.outbox

import app.roleparty.core.rabbitmq.RabbitMqEvent
import app.roleparty.core.rabbitmq.RabbitMqMessage
import app.roleparty.core.rabbitmq.toRabbitMqMessage
import kotlin.time.Instant
import kotlin.uuid.Uuid
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.insert

/**
 * Stores [event] in the transaction that changes the state it describes, so that it's published if and only if that
 * transaction commits.
 */
suspend inline fun <reified E : RabbitMqEvent> R2dbcTransaction.enqueue(
    event: E,
    occurredAt: Instant,
    id: Uuid = Uuid.generateV7(),
) = enqueue(event.toRabbitMqMessage(id), occurredAt)

@PublishedApi
internal suspend fun enqueue(message: RabbitMqMessage, occurredAt: Instant) {
    OutboxEvents.insert {
        it[id] = message.id
        it[exchange] = message.exchange
        it[type] = message.type
        it[payload] = message.body
        it[OutboxEvents.occurredAt] = occurredAt
    }
}
