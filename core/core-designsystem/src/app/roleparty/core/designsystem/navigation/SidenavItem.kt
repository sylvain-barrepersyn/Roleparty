package app.roleparty.core.designsystem.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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

@Composable
fun SidenavItem(
    icon: IconVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = if (selected || hovered) LocalElevation.current.backgroundColor(LocalElevationLevel.current + 1) else Color.Transparent
    val textColor = if (selected) Theme.colors.textPrimary else if (hovered) Theme.colors.textPrimary else Theme.colors.textSecondary
    val iconColor = if (selected) Theme.colors.primary else if (hovered) Theme.colors.textPrimary else Theme.colors.textSecondary

    Column(
        modifier = modifier
            .size(88.dp, 56.dp)
            .clip(Theme.shapes.roundedSmall)
            .background(background)
            .selectable(selected = selected, interactionSource = interactionSource, indication = null, role = Role.Tab, onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(vector = icon, tint = iconColor, size = 24.dp)
        Text(text = label, style = Theme.typography.bodySmall, color = textColor, maxLines = 1)
    }
}
