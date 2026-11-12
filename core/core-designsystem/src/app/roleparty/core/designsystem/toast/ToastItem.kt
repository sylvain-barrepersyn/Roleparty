package app.roleparty.core.designsystem.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.designsystem.resources.Res
import app.roleparty.designsystem.resources.toast_error
import app.roleparty.designsystem.resources.toast_info
import app.roleparty.designsystem.resources.toast_success
import app.roleparty.designsystem.resources.toast_warning
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val TRANSITION_MILLIS = 300
private val TOAST_SHADOW = 8.dp

@Composable
internal fun ToastItem(toast: ToastData, onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val dismiss by rememberUpdatedState(onDismiss)

    LaunchedEffect(toast.id) {
        visible = true
        delay(toast.duration)
        visible = false
        delay(TRANSITION_MILLIS.milliseconds)
        dismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(tween(TRANSITION_MILLIS)) { it } + fadeIn(tween(TRANSITION_MILLIS)),
        exit = slideOutHorizontally(tween(TRANSITION_MILLIS)) { it } + fadeOut(tween(TRANSITION_MILLIS)),
    ) {
        Surface(modifier = Modifier.shadow(TOAST_SHADOW, Theme.shapes.roundedSmall), shape = Theme.shapes.roundedSmall) {
            Row(
                modifier = Modifier.padding(16.dp).widthIn(max = 300.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    vector = toast.type.icon(),
                    tint = if (toast.type == ToastType.ERROR) Theme.colors.error else Theme.colors.primary,
                    contentDescription = toast.type.description(),
                )
                Text(text = toast.message(), color = Theme.colors.textPrimary, modifier = Modifier.weight(1f, fill = false))
                toast.action?.invoke()
            }
        }
    }
}

private fun ToastType.icon() = when (this) {
    ToastType.SUCCESS -> IconVector.CONFIRM
    ToastType.ERROR -> IconVector.CANCEL
    ToastType.INFO -> IconVector.NOTIFICATION
    ToastType.WARNING -> IconVector.ASTERISK
}

@Composable
private fun ToastType.description() = stringResource(
    when (this) {
        ToastType.SUCCESS -> Res.string.toast_success
        ToastType.ERROR -> Res.string.toast_error
        ToastType.INFO -> Res.string.toast_info
        ToastType.WARNING -> Res.string.toast_warning
    }
)
