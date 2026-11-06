package app.roleparty.core.designsystem.dialog

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import app.roleparty.core.designsystem.elevation.LocalElevationLevel
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.theme.Theme
import androidx.compose.ui.window.Dialog as ComposeDialog

private val DIALOG_SHADOW = 16.dp

@Composable
fun Dialog(
    displayed: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.shapes.rounded,
    content: @Composable BoxScope.() -> Unit,
) {
    if (!displayed) return

    ComposeDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        CompositionLocalProvider(LocalElevationLevel provides 0) {
            Surface(
                modifier = modifier.shadow(DIALOG_SHADOW, shape),
                shape = shape,
                content = content,
            )
        }
    }
}
