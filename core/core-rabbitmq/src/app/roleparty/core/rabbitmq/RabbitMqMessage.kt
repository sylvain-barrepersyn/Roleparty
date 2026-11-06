package app.roleparty.core.rabbitmq

import kotlin.reflect.KClass
import kotlin.uuid.Uuid
import kotlinx.serialization.json.Json

data class RabbitMqMessage(
    val id: Uuid,
    val exchange: String,
    val type: String,
    val body: String,
)

@PublishedApi
internal val rabbitMqJson = Json { ignoreUnknownKeys = true }

inline fun <reified E : RabbitMqEvent> E.toRabbitMqMessage(id: Uuid) = RabbitMqMessage(
    id = id,
    exchange = E::class.declaredConstant("EXCHANGE"),
    type = E::class.java.name,
    body = rabbitMqJson.encodeToString(this),
)

@PublishedApi
internal fun KClass<*>.declaredConstant(name: String): String =
    java.fields.firstOrNull { it.name == name }?.get(null) as String?
        ?: error("Impossible to get $name for $simpleName")
