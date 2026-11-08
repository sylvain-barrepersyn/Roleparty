package app.roleparty.core.designsystem.field.number

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import app.roleparty.core.designsystem.field.text.StringField

@Composable
fun IntField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    required: Boolean = false,
    error: String? = null,
    allowNegative: Boolean = false,
) {
    val pattern = if (allowNegative) NEGATIVE_INTEGER else POSITIVE_INTEGER

    StringField(
        value = value?.toString().orEmpty(),
        onValueChange = { text ->
            when {
                text.isEmpty() -> onValueChange(null)
                text == "-" && allowNegative -> Unit
                pattern.matches(text) -> text.toIntOrNull()?.let(onValueChange)
            }
        },
        label = label,
        modifier = modifier,
        enabled = enabled,
        required = required,
        error = error,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

private val POSITIVE_INTEGER = Regex("\\d{1,9}")
private val NEGATIVE_INTEGER = Regex("-?\\d{1,9}")
