package app.roleparty.core.test

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

class ControlledClock(private var now: Instant = Instant.fromEpochMilliseconds(0)) : Clock {

    override fun now() = now

    fun setTo(instant: Instant) {
        now = instant
    }

    fun advanceBy(duration: Duration) {
        now += duration
    }
}
