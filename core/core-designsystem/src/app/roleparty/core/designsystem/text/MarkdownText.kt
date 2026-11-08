package app.roleparty.core.designsystem.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import app.roleparty.core.designsystem.theme.Theme
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichText

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Theme.typography.body,
    color: Color = style.color,
) {
    val state = remember(markdown) { RichTextState().apply { setMarkdown(markdown) } }
    BasicRichText(state = state, modifier = modifier, style = style.copy(color = color))
}
