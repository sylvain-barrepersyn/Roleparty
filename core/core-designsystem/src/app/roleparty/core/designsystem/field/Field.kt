package app.roleparty.core.designsystem.field

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.divider.HorizontalDivider
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.icon.iconTint
import app.roleparty.core.designsystem.surface.SurfaceContainer
import app.roleparty.core.designsystem.surface.SurfaceInteraction
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf
import app.roleparty.designsystem.resources.Res
import app.roleparty.designsystem.resources.required as requiredString
import org.jetbrains.compose.resources.stringResource

@Composable
fun Field(
    label: String,
    modifier: Modifier = Modifier,
    icon: IconVector? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    error: String? = null,
    shape: Shape = Theme.shapes.rounded,
    headerTrailing: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    var focused by remember { mutableStateOf(false) }

    val visibleError = error.takeIf { enabled }
    val borderColor = when {
        !enabled -> null
        visibleError != null -> Theme.colors.error
        focused -> Theme.colors.primary
        else -> null
    }
    val errorSemantics = if (visibleError != null) Modifier.semantics { error(visibleError) } else Modifier

    SurfaceContainer(
        modifier = modifier
            .onFocusChanged { focused = it.hasFocus }
            .then(errorSemantics),
        shape = shape,
        contentAlignment = Alignment.TopStart,
        enabled = enabled,
        interaction = SurfaceInteraction.Hoverable,
        borderColor = borderColor,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FieldLabel(label = label, icon = icon, enabled = enabled, focused = focused, required = required)
                Spacer(modifier = Modifier.weight(1f))
                headerTrailing?.invoke(this)
            }
            HorizontalDivider()
            Column(modifier = Modifier.fillMaxWidth(), content = content)
            visibleError?.let {
                HorizontalDivider()
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    style = Theme.typography.bodySmall,
                    color = Theme.colors.error,
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(
    label: String,
    icon: IconVector?,
    enabled: Boolean,
    focused: Boolean,
    required: Boolean,
) {
    val tint = iconTint(enabled)
    val labelColor = (if (focused) Theme.colors.textPrimary else Theme.colors.textSecondary).disabledIf(!enabled)

    Row(
        modifier = Modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { Icon(vector = it, tint = tint, size = 20.dp) }

        Text(text = label, color = labelColor)

        if (required) {
            Icon(
                vector = IconVector.ASTERISK,
                tint = tint,
                contentDescription = stringResource(Res.string.requiredString),
                size = 10.dp,
            )
        }
    }
}
