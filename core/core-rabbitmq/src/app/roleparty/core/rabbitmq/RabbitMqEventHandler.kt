package app.roleparty.core.rabbitmq

import kotlin.uuid.Uuid

/**
 * Handles an event from the queue declared in a `const val QUEUE` of its companion object.
 *
 * An event can be delivered more than once, so handlers must ignore an [eventId] they've already handled. A handler
 * that throws sends the event to the queue's dead-letter queue.
 */
interface RabbitMqEventHandler<E : RabbitMqEvent> {

    suspend fun handle(eventId: Uuid, event: E)
}
