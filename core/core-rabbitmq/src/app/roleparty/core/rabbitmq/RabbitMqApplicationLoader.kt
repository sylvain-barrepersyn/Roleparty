package app.roleparty.core.rabbitmq

import app.roleparty.core.commons.requireValueOrFile
import com.rabbitmq.client.ConnectionFactory
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.util.AttributeKey

private const val HOST = "RABBITMQ_HOST"
private const val PORT = "RABBITMQ_PORT"
private const val USER = "RABBITMQ_USER"
private const val PASSWORD = "RABBITMQ_PASSWORD"

private val RabbitMqKey = AttributeKey<RabbitMq>("RabbitMq")

fun Application.loadRabbitMq(env: Map<String, String> = System.getenv()): RabbitMq {
    attributes.getOrNull(RabbitMqKey)?.let { return it }

    val connection = ConnectionFactory().apply {
        host = env.requireValueOrFile(HOST)
        port = env.requireValueOrFile(PORT).toInt()
        username = env.requireValueOrFile(USER)
        password = env.requireValueOrFile(PASSWORD)
    }.newConnection()

    monitor.subscribe(ApplicationStopped) {
        if (connection.isOpen) connection.close()
    }

    return RabbitMq(connection, environment.log).also { attributes.put(RabbitMqKey, it) }
}
