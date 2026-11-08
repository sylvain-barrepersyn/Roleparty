package app.roleparty.core.designsystem.feedback

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme

enum class CalloutKind { INFORMATION, ERROR }

@Composable
fun Callout(
    text: String,
    modifier: Modifier = Modifier,
    kind: CalloutKind = CalloutKind.INFORMATION,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val color = when (kind) {
        CalloutKind.INFORMATION -> Theme.colors.textSecondary
        CalloutKind.ERROR -> Theme.colors.error
    }

    Surface(modifier = modifier.border(Theme.borderWidth, color, Theme.shapes.rounded)) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(vector = if (kind == CalloutKind.ERROR) IconVector.CANCEL else IconVector.NOTIFICATION, tint = color, size = 20.dp)
            Text(text = text, color = color, modifier = Modifier.weight(1f))
            actions?.invoke(this)
        }
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(vector = IconVector.EMPTY, tint = Theme.colors.textSecondary, size = 20.dp)
        Text(text = text)
    }
}
