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
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.divider.HorizontalDivider
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.SurfaceContainer
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabled

@Composable
fun Field(
    label: String,
    modifier: Modifier = Modifier,
    icon: IconVector? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    requiredDescription: String? = null,
    isError: Boolean = false,
    shape: Shape = Theme.shapes.rounded,
    valueDisplay: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    var focused by remember { mutableStateOf(false) }

    val borderColor = when {
        !enabled -> null
        isError -> Theme.colors.error
        focused -> Theme.colors.primary
        else -> null
    }

    SurfaceContainer(
        modifier = modifier.onFocusChanged { focused = it.hasFocus },
        shape = shape,
        contentAlignment = Alignment.TopStart,
        enabled = enabled,
        reactsToHover = true,
        borderColor = borderColor,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FieldLabel(
                    label = label,
                    icon = icon,
                    enabled = enabled,
                    focused = focused,
                    required = required,
                    requiredDescription = requiredDescription,
                )
                Spacer(modifier = Modifier.weight(1f))
                valueDisplay?.invoke(this)
            }
            HorizontalDivider()
            Column(modifier = Modifier.fillMaxWidth(), content = content)
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
    requiredDescription: String?,
) {
    val iconColor = if (enabled) Theme.colors.primary else Theme.colors.textSecondary.disabled()
    val labelColor = when {
        !enabled -> Theme.colors.textSecondary.disabled()
        focused -> Theme.colors.textPrimary
        else -> Theme.colors.textSecondary
    }

    Row(
        modifier = Modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { Icon(vector = it, tint = iconColor, size = 20.dp) }

        Text(text = label, color = labelColor)

        if (required) {
            Icon(vector = IconVector.ASTERISK, tint = iconColor, contentDescription = requiredDescription, size = 10.dp)
        }
    }
}
