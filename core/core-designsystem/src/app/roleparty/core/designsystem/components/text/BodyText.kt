package app.roleparty.core.designsystem.components.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.textSecondary,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = Theme.typography.body.copy(color = color)
    )
}