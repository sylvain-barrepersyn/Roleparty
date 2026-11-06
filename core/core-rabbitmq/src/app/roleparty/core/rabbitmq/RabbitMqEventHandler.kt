package app.roleparty.core.rabbitmq

import kotlin.uuid.Uuid

interface RabbitMqEventHandler<E : RabbitMqEvent> {

    suspend fun handle(eventId: Uuid, event: E)
}
