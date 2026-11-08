package app.roleparty.security.domain.service

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

internal class AttemptLimitingService(
    private val maxAttempts: Int,
    private val window: Duration,
    private val clock: Clock,
) {
    private val attempts = mutableMapOf<String, Attempts>()

    @Synchronized
    fun isBlocked(key: String): Boolean = current(key)?.let { it.count >= maxAttempts } ?: false

    @Synchronized
    fun record(key: String) {
        attempts.entries.removeIf { it.value.expiration <= clock.now() }
        val existing = current(key)
        attempts[key] = Attempts((existing?.count ?: 0) + 1, existing?.expiration ?: (clock.now() + window))
    }

    @Synchronized
    fun reset(key: String) {
        attempts.remove(key)
    }

    private fun current(key: String) = attempts[key]?.takeIf { it.expiration > clock.now() }

    private data class Attempts(val count: Int, val expiration: Instant)
}
