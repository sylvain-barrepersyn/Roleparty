package app.roleparty.core.designsystem.field.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.field.Field
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf
import app.roleparty.designsystem.resources.Res
import app.roleparty.designsystem.resources.bold
import app.roleparty.designsystem.resources.bulleted_list
import app.roleparty.designsystem.resources.heading_1
import app.roleparty.designsystem.resources.heading_2
import app.roleparty.designsystem.resources.italic
import com.mohamedrejeb.richeditor.model.HeadingStyle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import org.jetbrains.compose.resources.stringResource

private val BOLD = SpanStyle(fontWeight = FontWeight.Bold)
private val ITALIC = SpanStyle(fontStyle = FontStyle.Italic)

@Composable
fun RichTextField(
    markdown: String,
    onMarkdownChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    required: Boolean = false,
    error: String? = null,
    minHeight: Dp = 160.dp,
    structured: Boolean = false,
) {
    val state = rememberRichTextState()
    val currentOnChange by rememberUpdatedState(onMarkdownChange)

    LaunchedEffect(markdown) {
        if (state.toMarkdown() != markdown) state.setMarkdown(markdown)
    }

    LaunchedEffect(state) {
        snapshotFlow { state.annotatedString }.collect {
            val current = state.toMarkdown()
            if (current != markdown) currentOnChange(current)
        }
    }

    Field(
        label = label,
        modifier = modifier,
        enabled = enabled,
        required = required,
        error = error,
        headerTrailing = { FormatButtons(state, enabled, structured) },
    ) {
        BasicRichTextEditor(
            state = state,
            enabled = enabled,
            textStyle = Theme.typography.body.copy(color = Theme.colors.textPrimary.disabledIf(!enabled)),
            cursorBrush = SolidColor(Theme.colors.textPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .padding(12.dp)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown || !(event.isCtrlPressed || event.isMetaPressed)) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.B -> true.also { state.toggleSpanStyle(BOLD) }
                        Key.I -> true.also { state.toggleSpanStyle(ITALIC) }
                        else -> false
                    }
                },
        )
    }
}

@Composable
private fun FormatButtons(state: RichTextState, enabled: Boolean, structured: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (structured) {
            HeadingButton(state, HeadingStyle.H1, IconVector.TEXT_HEADING_1, stringResource(Res.string.heading_1), enabled)
            HeadingButton(state, HeadingStyle.H2, IconVector.TEXT_HEADING_2, stringResource(Res.string.heading_2), enabled)
            IconButton(
                icon = IconVector.TEXT_LIST,
                contentDescription = stringResource(Res.string.bulleted_list),
                onClick = { state.toggleUnorderedList() },
                enabled = enabled,
                size = ButtonSize.TINY,
                modifier = Modifier.focusProperties { canFocus = false },
            )
        }
        IconButton(
            icon = IconVector.TEXT_BOLD,
            contentDescription = stringResource(Res.string.bold),
            onClick = { state.toggleSpanStyle(BOLD) },
            enabled = enabled,
            size = ButtonSize.TINY,
            modifier = Modifier.focusProperties { canFocus = false },
        )
        IconButton(
            icon = IconVector.TEXT_ITALIC,
            contentDescription = stringResource(Res.string.italic),
            onClick = { state.toggleSpanStyle(ITALIC) },
            enabled = enabled,
            size = ButtonSize.TINY,
            modifier = Modifier.focusProperties { canFocus = false },
        )
    }
}

@Composable
private fun HeadingButton(state: RichTextState, heading: HeadingStyle, icon: IconVector, label: String, enabled: Boolean) {
    IconButton(
        icon = icon,
        contentDescription = label,
        onClick = { state.setHeadingStyle(if (state.currentHeadingStyle == heading) HeadingStyle.Normal else heading) },
        enabled = enabled,
        size = ButtonSize.TINY,
        modifier = Modifier.focusProperties { canFocus = false },
    )
}
