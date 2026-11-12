package app.roleparty.core.designsystem.toast

import androidx.compose.runtime.Composable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

enum class ToastType(internal val duration: Duration) {
    SUCCESS(2.seconds),
    ERROR(5.seconds),
    INFO(5.seconds),
    WARNING(5.seconds),
}

class ToastData internal constructor(
    val id: Uuid,
    val type: ToastType,
    val duration: Duration,
    val action: (@Composable () -> Unit)?,
    val message: @Composable () -> String,
)
