package app.roleparty.core.designsystem.field.select

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.elevation.LocalElevation
import app.roleparty.core.designsystem.elevation.LocalElevationLevel
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf

@Composable
fun FieldOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role = Role.RadioButton,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    val background = if (enabled && hovered) {
        LocalElevation.current.backgroundColor(LocalElevationLevel.current + 1)
    } else {
        Color.Transparent
    }
    val contentColor = (if (selected) Theme.colors.textPrimary else Theme.colors.textSecondary).disabledIf(!enabled)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .clip(Theme.shapes.rounded)
            .background(background)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = role,
                onClick = onClick,
            )
            .then(if (enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = contentColor)

        Spacer(modifier = Modifier.weight(1f))

        trailingContent?.invoke(this)

        Icon(
            vector = if (selected) IconVector.CONFIRM else IconVector.EMPTY,
            tint = contentColor,
            size = 20.dp,
        )
    }
}
