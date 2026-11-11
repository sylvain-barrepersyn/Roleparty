package app.roleparty.core.designsystem.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
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
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun TabGroup(modifier: Modifier = Modifier, tabs: @Composable RowScope.() -> Unit) {
    Surface(modifier = modifier.height(40.dp), shape = Theme.shapes.roundedSmall) {
        Row(
            modifier = Modifier.selectableGroup(),
            content = tabs,
        )
    }
}

@Composable
fun RowScope.Tab(label: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) LocalElevation.current.backgroundColor(LocalElevationLevel.current + 1) else Color.Transparent

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(Theme.shapes.roundedSmall)
            .background(background)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = if (selected) Theme.colors.textPrimary else Theme.colors.textSecondary)
    }
}
