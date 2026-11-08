package app.roleparty.core.designsystem.field.select

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.field.Field
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf

@Composable
fun <T> MultiSelectField(
    selectedValues: Set<T>,
    onSelectionChange: (Set<T>) -> Unit,
    options: List<T>,
    label: String,
    modifier: Modifier = Modifier,
    optionLabel: @Composable (T) -> String = { it.toString() },
    sortByLabel: Boolean = false,
    maxHeaderDisplayCount: Int = 3,
    maxListHeight: Dp = 240.dp,
    icon: IconVector? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    error: String? = null,
    shape: Shape = Theme.shapes.roundedSmall,
) {
    val labeled = labeledOptions(options, optionLabel, sortByLabel)

    Field(
        label = label,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        required = required,
        error = error,
        shape = shape,
        headerTrailing = {
            val selectedLabels = labeled.filter { it.value in selectedValues }.map { it.label }
            if (selectedLabels.isNotEmpty()) {
                Text(
                    text = selectionSummary(selectedLabels, maxHeaderDisplayCount),
                    color = Theme.colors.textPrimary.disabledIf(!enabled),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    ) {
        ScrollableOptionList(maxHeight = maxListHeight) {
            labeled.forEach { option ->
                val selected = option.value in selectedValues
                FieldOptionRow(
                    label = option.label,
                    selected = selected,
                    onClick = {
                        onSelectionChange(if (selected) selectedValues - option.value else selectedValues + option.value)
                    },
                    enabled = enabled,
                    role = Role.Checkbox,
                )
            }
        }
    }
}

private fun selectionSummary(labels: List<String>, maxDisplayCount: Int): String {
    val displayed = labels.take(maxDisplayCount).joinToString()
    val remaining = labels.size - maxDisplayCount
    return if (remaining > 0) "$displayed + $remaining" else displayed
}
