package app.roleparty.core.designsystem.toast

import androidx.compose.runtime.Composable
import kotlin.time.Duration
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object Toast {

    private val mutableToasts = MutableStateFlow<List<ToastData>>(emptyList())
    val toasts: StateFlow<List<ToastData>> = mutableToasts.asStateFlow()

    fun show(
        type: ToastType = ToastType.INFO,
        duration: Duration = type.duration,
        action: (@Composable () -> Unit)? = null,
        message: @Composable () -> String,
    ) {
        mutableToasts.update { it + ToastData(Uuid.generateV7(), type, duration, action, message) }
    }

    fun remove(toastId: Uuid) {
        mutableToasts.update { toasts -> toasts.filterNot { it.id == toastId } }
    }
}
