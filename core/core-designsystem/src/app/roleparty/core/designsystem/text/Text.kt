package app.roleparty.core.designsystem.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Theme.typography.body,
    color: Color = style.color,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = overflow,
    )
}
