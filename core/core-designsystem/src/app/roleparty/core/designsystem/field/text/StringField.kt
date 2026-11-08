package app.roleparty.core.designsystem.field.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.field.Field
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf

@Composable
fun StringField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    icon: IconVector? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    error: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = Theme.shapes.roundedSmall,
) {
    val placeholderColor = Theme.colors.textSecondary.disabledIf(!enabled)

    Field(
        label = label,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        required = required,
        error = error,
        shape = shape,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            textStyle = Theme.typography.body.copy(color = Theme.colors.textPrimary.disabledIf(!enabled)),
            cursorBrush = SolidColor(Theme.colors.textPrimary),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(text = placeholder, color = placeholderColor, maxLines = 1)
                    }
                    innerTextField()
                }
            }
        )
    }
}
