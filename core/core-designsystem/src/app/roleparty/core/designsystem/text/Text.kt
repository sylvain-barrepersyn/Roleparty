package app.roleparty.core.designsystem.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Theme.typography.body,
    color: Color = style.color,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color)
    )
}
