package app.roleparty.core.outbox

import org.slf4j.Logger

private const val REMINDER_INTERVAL = 300

internal class RelayFailureLog(private val log: Logger) {
    private var consecutiveFailures = 0

    fun failed(exception: Exception) {
        consecutiveFailures++
        when {
            consecutiveFailures == 1 -> log.error("Impossible to relay outbox events, trying again until it works", exception)
            consecutiveFailures % REMINDER_INTERVAL == 0 ->
                log.warn("Still impossible to relay outbox events after $consecutiveFailures attempts: ${exception.message}")
            else -> log.debug("Impossible to relay outbox events: ${exception.message}")
        }
    }

    fun succeeded() {
        if (consecutiveFailures > 0) log.info("Relaying outbox events again after $consecutiveFailures failed attempts")
        consecutiveFailures = 0
    }
}
