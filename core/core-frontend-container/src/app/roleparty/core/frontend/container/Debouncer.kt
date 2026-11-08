package app.roleparty.core.frontend.container

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debouncer(private val scope: CoroutineScope, private val delay: Duration = 700.milliseconds) {

    private val jobs = mutableMapOf<Any, Job>()

    fun submit(key: Any, action: suspend () -> Unit) {
        jobs[key]?.cancel()
        jobs[key] = scope.launch {
            delay(delay)
            action()
        }
    }
}
