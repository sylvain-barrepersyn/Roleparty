package app.roleparty.core.designsystem.field.select

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import app.roleparty.core.designsystem.divider.HorizontalDivider
import app.roleparty.core.designsystem.elevation.LocalElevation
import app.roleparty.core.designsystem.elevation.LocalElevationLevel
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf

data class ChoiceColumn<C>(val value: C, val label: String)

private const val LABEL_WEIGHT = 2f

@Composable
fun <R, C> ChoiceTable(
    rows: List<R>,
    columns: List<ChoiceColumn<C>>,
    rowLabel: @Composable RowScope.(R) -> Unit,
    selected: (R) -> C?,
    onSelect: (R, C) -> Unit,
    modifier: Modifier = Modifier,
    enabled: (R) -> Boolean = { true },
    available: (R, C) -> Boolean = { _, _ -> true },
) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Bottom) {
            Box(Modifier.weight(LABEL_WEIGHT))
            columns.forEach { column ->
                Box(Modifier.weight(1f).padding(horizontal = 4.dp), contentAlignment = Alignment.BottomCenter) {
                    Text(text = column.label, style = Theme.typography.bodySmall, color = Theme.colors.textSecondary)
                }
            }
        }
        HorizontalDivider()
        rows.forEach { row ->
            val rowEnabled = enabled(row)
            val current = selected(row)
            Row(Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(LABEL_WEIGHT).padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) { rowLabel(row) }
                columns.forEach { column ->
                    Box(Modifier.weight(1f).height(44.dp), contentAlignment = Alignment.Center) {
                        if (available(row, column.value)) {
                            ChoiceCell(
                                selected = current == column.value,
                                enabled = rowEnabled,
                                onClick = { onSelect(row, column.value) },
                            )
                        }
                    }
                }
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun ChoiceCell(selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = if (enabled && hovered) LocalElevation.current.backgroundColor(LocalElevationLevel.current + 1) else Color.Transparent
    val tint = (if (selected) Theme.colors.primary else Theme.colors.textSecondary).disabledIf(!enabled)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(Theme.shapes.rectangle)
            .background(background)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .then(if (enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(vector = if (selected) IconVector.CONFIRM else IconVector.EMPTY, tint = tint, size = 20.dp)
    }
}
