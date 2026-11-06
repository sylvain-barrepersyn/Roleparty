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
fun <T : Any> SingleSelectField(
    value: T?,
    onValueChange: (T) -> Unit,
    options: List<T>,
    label: String,
    modifier: Modifier = Modifier,
    optionLabel: @Composable (T) -> String = { it.toString() },
    sortByLabel: Boolean = false,
    maxListHeight: Dp = 240.dp,
    icon: IconVector? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    error: String? = null,
    shape: Shape = Theme.shapes.rounded,
) {
    Field(
        label = label,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        required = required,
        error = error,
        shape = shape,
        headerTrailing = {
            value?.let {
                Text(
                    text = optionLabel(it),
                    color = Theme.colors.textPrimary.disabledIf(!enabled),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    ) {
        ScrollableOptionList(maxHeight = maxListHeight) {
            labeledOptions(options, optionLabel, sortByLabel).forEach { option ->
                FieldOptionRow(
                    label = option.label,
                    selected = option.value == value,
                    onClick = { onValueChange(option.value) },
                    enabled = enabled,
                    role = Role.RadioButton,
                )
            }
        }
    }
}
