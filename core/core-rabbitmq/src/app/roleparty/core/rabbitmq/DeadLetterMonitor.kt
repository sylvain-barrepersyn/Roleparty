package app.roleparty.core.rabbitmq

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private val CHECK_INTERVAL = 1.minutes

fun Application.monitorDeadLetters(
    rabbitMq: RabbitMq,
    interval: Duration = CHECK_INTERVAL,
    alert: suspend (queue: String, messages: Long) -> Unit,
) {
    val alerted = mutableMapOf<String, Long>()

    val job = launch {
        while (isActive) {
            try {
                rabbitMq.deadLetterCounts().forEach { (val key, val value) ->
                    val queue = key
                    val messages = value
                    if (messages > alerted.getOrDefault(queue, 0)) {
                        environment.log.error("Dead-letter queue $queue holds $messages message(s) that could not be handled")
                        alert(queue, messages)
                    }
                    alerted[queue] = messages
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                environment.log.warn("Impossible to check dead-letter queues: ${exception.message}")
            }
            delay(interval)
        }
    }

    monitor.subscribe(ApplicationStopping) { job.cancel() }
}
