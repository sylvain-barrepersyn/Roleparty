package app.roleparty.core.designsystem.field.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.field.select.FieldOptionRow
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf
import app.roleparty.designsystem.resources.*
import app.roleparty.core.document.Document
import app.roleparty.core.document.Paragraph
import app.roleparty.core.document.ParagraphKind
import app.roleparty.core.document.TextRun
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val BOLD = SpanStyle(fontWeight = FontWeight.Bold)
private val ITALIC = SpanStyle(fontStyle = FontStyle.Italic)
private val HANDLE_SIZE = 24.dp
private val LEVEL_INDENT = 20.dp
private val BULLETS = listOf("•", "◦", "▪")

private val ParagraphKind.label: StringResource
    get() = when (this) {
        ParagraphKind.TEXT -> Res.string.paragraph_text
        ParagraphKind.TITLE -> Res.string.heading_1
        ParagraphKind.SUBTITLE -> Res.string.heading_2
        ParagraphKind.BULLET -> Res.string.bulleted_list
    }

@Composable
fun ParagraphsField(
    document: Document,
    onDocumentChange: (Document) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    required: Boolean = false,
    error: String? = null,
    structured: Boolean = false,
) {
    val editor = remember { ParagraphsEditor(document) }
    val currentOnChange by rememberUpdatedState(onDocumentChange)

    LaunchedEffect(document) {
        if (document != editor.lastDocument) editor.load(document)
    }

    LaunchedEffect(editor) {
        snapshotFlow { editor.paragraphs.map { Triple(it.kind, it.level, it.state.annotatedString) } }.collect {
            editor.splitPastedLines()
            val current = editor.document()
            if (current != editor.lastDocument) {
                editor.lastDocument = current
                currentOnChange(current)
            }
        }
    }

    LaunchedEffect(editor.pendingFocus) {
        val target = editor.pendingFocus ?: return@LaunchedEffect
        withFrameNanos { }
        editor.paragraphs.firstOrNull { it.id == target.id }?.let {
            it.focusRequester.requestFocus()
            it.state.selection = TextRange(target.position.coerceIn(0, it.length))
        }
        editor.pendingFocus = null
    }

    var focused by remember { mutableStateOf(false) }

    Surface(modifier = modifier.onFocusChanged { focused = it.hasFocus }, shape = Theme.shapes.roundedSmall) {
        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            editor.paragraphs.forEach { paragraph ->
                key(paragraph.id) {
                    ParagraphRow(editor, paragraph, enabled, structured)
                }
            }
            error?.takeIf { enabled }?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    style = Theme.typography.bodySmall,
                    color = Theme.colors.error,
                )
            }
        }
    }
}

@Composable
private fun ParagraphRow(editor: ParagraphsEditor, paragraph: EditedParagraph, enabled: Boolean, structured: Boolean) {
    val hoverSource = remember { MutableInteractionSource() }
    val hovered by hoverSource.collectIsHoveredAsState()
    val dragging = editor.draggedId == paragraph.id
    val textStyle = paragraph.kind.textStyle().copy(color = Theme.colors.textPrimary.disabledIf(!enabled))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (dragging) 1f else 0f)
            .graphicsLayer { translationY = if (dragging) editor.dragOffset else 0f }
            .onSizeChanged { editor.heights[paragraph.id] = it.height }
            .hoverable(hoverSource),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.padding(4.dp)) {
            Handle(
                editor = editor,
                paragraph = paragraph,
                visible = enabled && (hovered || dragging || editor.focusedId == paragraph.id || editor.menuId == paragraph.id),
                enabled = enabled,
                structured = structured,
            )
        }
        if (paragraph.kind == ParagraphKind.BULLET) {
            Spacer(Modifier.width(LEVEL_INDENT * paragraph.level))
            Text(
                text = BULLETS[paragraph.level % BULLETS.size],
                style = textStyle,
                modifier = Modifier.width(16.dp).padding(start = 4.dp)
            )
        }
        BasicRichTextEditor(
            state = paragraph.state,
            enabled = enabled,
            textStyle = textStyle,
            cursorBrush = SolidColor(Theme.colors.textPrimary),
            modifier = Modifier
                .weight(1f)
                .focusRequester(paragraph.focusRequester)
                .onFocusChanged { if (it.isFocused) editor.focusedId = paragraph.id }
                .onPreviewKeyEvent { editor.onKey(paragraph, it) },
        )
    }
}

@Composable
private fun Handle(
    editor: ParagraphsEditor,
    paragraph: EditedParagraph,
    visible: Boolean,
    enabled: Boolean,
    structured: Boolean
) {
    Box(
        modifier = Modifier
            .size(HANDLE_SIZE)
            .alpha(if (visible) 1f else 0f)
            .then(if (enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
            .pointerInput(paragraph.id, enabled) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragStart = { editor.startDrag(paragraph.id) },
                    onDrag = { change, amount ->
                        change.consume()
                        editor.drag(amount.y)
                    },
                    onDragEnd = { editor.endDrag() },
                    onDragCancel = { editor.endDrag() },
                )
            }
            .clickable(enabled = enabled && structured, role = Role.Button) { editor.menuId = paragraph.id },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            vector = IconVector.DRAG_HANDLE,
            contentDescription = stringResource(Res.string.paragraph_handle),
            tint = Theme.colors.textSecondary,
            size = 18.dp
        )
        if (editor.menuId == paragraph.id) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, (HANDLE_SIZE.value * 1.2f).toInt()),
                onDismissRequest = { editor.menuId = null },
                properties = PopupProperties(focusable = true),
            ) {
                Surface(modifier = Modifier.width(220.dp).padding(4.dp)) {
                    Column(Modifier.padding(vertical = 4.dp)) {
                        ParagraphKind.entries.forEach { kind ->
                            FieldOptionRow(
                                label = stringResource(kind.label),
                                selected = paragraph.kind == kind,
                                onClick = { editor.changeKind(paragraph, kind) },
                                role = Role.RadioButton,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParagraphKind.textStyle(): TextStyle = when (this) {
    ParagraphKind.TITLE -> Theme.typography.headline
    ParagraphKind.SUBTITLE -> Theme.typography.title
    ParagraphKind.TEXT, ParagraphKind.BULLET -> Theme.typography.body
}

@Composable
private fun FormatButtons(editor: ParagraphsEditor, enabled: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            icon = IconVector.TEXT_BOLD,
            contentDescription = stringResource(Res.string.bold),
            onClick = { editor.focused()?.state?.toggleSpanStyle(BOLD) },
            enabled = enabled,
            size = ButtonSize.TINY,
            modifier = Modifier.focusProperties { canFocus = false },
        )
        IconButton(
            icon = IconVector.TEXT_ITALIC,
            contentDescription = stringResource(Res.string.italic),
            onClick = { editor.focused()?.state?.toggleSpanStyle(ITALIC) },
            enabled = enabled,
            size = ButtonSize.TINY,
            modifier = Modifier.focusProperties { canFocus = false },
        )
    }
}

private class EditedParagraph(val id: Long, kind: ParagraphKind, runs: List<TextRun>, level: Int) {
    var kind by mutableStateOf(kind)
    var level by mutableStateOf(level)
    val state = RichTextState().apply { setRuns(runs) }
    val focusRequester = FocusRequester()

    val length get() = state.annotatedString.text.length

    fun runs(range: TextRange = TextRange(0, length)) = state.runs(range)

    fun toParagraph() = Paragraph(kind, level, runs())
}

private fun RichTextState.runs(range: TextRange): List<TextRun> {
    val text = annotatedString.text
    val bold = BooleanArray(text.length)
    val italic = BooleanArray(text.length)
    annotatedString.spanStyles.forEach { span ->
        for (index in span.start.coerceAtLeast(0) until span.end.coerceAtMost(text.length)) {
            if ((span.item.fontWeight?.weight ?: 0) >= FontWeight.Bold.weight) bold[index] = true
            if (span.item.fontStyle == FontStyle.Italic) italic[index] = true
        }
    }
    val runs = mutableListOf<TextRun>()
    for (index in range.min.coerceIn(0, text.length) until range.max.coerceIn(0, text.length)) {
        val last = runs.lastOrNull()
        if (last != null && last.bold == bold[index] && last.italic == italic[index]) runs[runs.lastIndex] = last.copy(text = last.text + text[index])
        else runs += TextRun(text[index].toString(), bold[index], italic[index])
    }
    return runs
}

private fun RichTextState.setRuns(runs: List<TextRun>) {
    setText(runs.joinToString("") { it.text }, TextRange(0))
    var offset = 0
    runs.forEach { run ->
        val range = TextRange(offset, offset + run.text.length)
        if (run.bold && !range.collapsed) addSpanStyle(BOLD, range)
        if (run.italic && !range.collapsed) addSpanStyle(ITALIC, range)
        offset += run.text.length
    }
}

private data class FocusTarget(val id: Long, val position: Int)

private class ParagraphsEditor(document: Document) {

    val paragraphs = mutableStateListOf<EditedParagraph>()
    val heights = mutableStateMapOf<Long, Int>()
    var lastDocument = document
    var focusedId by mutableStateOf<Long?>(null)
    var menuId by mutableStateOf<Long?>(null)
    var draggedId by mutableStateOf<Long?>(null)
    var dragOffset by mutableStateOf(0f)
    var pendingFocus by mutableStateOf<FocusTarget?>(null)
    private var nextId = 0L

    init {
        fill(document)
    }

    fun load(document: Document) {
        lastDocument = document
        paragraphs.clear()
        heights.clear()
        fill(document)
    }

    private fun fill(document: Document) {
        val source = document.paragraphs.ifEmpty { listOf(Paragraph()) }
        paragraphs.addAll(source.map { create(it.kind, it.runs, it.level) })
    }

    private fun create(kind: ParagraphKind, runs: List<TextRun>, level: Int = 0) = EditedParagraph(nextId++, kind, runs, level)

    fun document() = Document.from(paragraphs.map { it.toParagraph() })

    private fun normalize() {
        var previous: Paragraph? = null
        paragraphs.forEach { paragraph ->
            paragraph.level = Document.allowedLevel(paragraph.kind, paragraph.level, previous)
            previous = Paragraph(paragraph.kind, paragraph.level)
        }
    }

    private fun subtree(paragraph: EditedParagraph): List<EditedParagraph> {
        val index = paragraphs.indexOf(paragraph)
        return paragraphs.drop(index + 1).takeWhile { it.kind == ParagraphKind.BULLET && it.level > paragraph.level }
    }

    private fun indent(paragraph: EditedParagraph): Boolean {
        val previous = paragraphs.getOrNull(paragraphs.indexOf(paragraph) - 1)
        if (previous?.kind != ParagraphKind.BULLET || paragraph.level > previous.level) return true
        (listOf(paragraph) + subtree(paragraph)).forEach { it.level++ }
        return true
    }

    private fun outdent(paragraph: EditedParagraph): Boolean {
        if (paragraph.level == 0) return true
        (listOf(paragraph) + subtree(paragraph)).forEach { it.level-- }
        normalize()
        return true
    }

    fun focused() = paragraphs.firstOrNull { it.id == focusedId }

    fun focusEnd() {
        paragraphs.lastOrNull()?.let { pendingFocus = FocusTarget(it.id, it.length) }
    }

    fun splitPastedLines() {
        val index = paragraphs.indexOfFirst { '\n' in it.state.annotatedString.text }
        if (index < 0) return
        val paragraph = paragraphs[index]
        val lines = mutableListOf(mutableListOf<TextRun>())
        paragraph.runs().forEach { run ->
            run.text.split('\n').forEachIndexed { position, part ->
                if (position > 0) lines += mutableListOf<TextRun>()
                if (part.isNotEmpty()) lines.last() += run.copy(text = part)
            }
        }
        paragraph.state.setRuns(lines.first())
        val bullet = paragraph.kind == ParagraphKind.BULLET
        val added = lines.drop(1).map { create(if (bullet) ParagraphKind.BULLET else ParagraphKind.TEXT, it, if (bullet) paragraph.level else 0) }
        paragraphs.addAll(index + 1, added)
        (added.lastOrNull() ?: paragraph).let { pendingFocus = FocusTarget(it.id, it.length) }
    }

    fun onKey(paragraph: EditedParagraph, event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        val selection = paragraph.state.selection
        val collapsed = selection.collapsed
        val shortcut = event.isCtrlPressed || event.isMetaPressed
        return when {
            shortcut && event.key == Key.B -> true.also { paragraph.state.toggleSpanStyle(BOLD) }
            shortcut && event.key == Key.I -> true.also { paragraph.state.toggleSpanStyle(ITALIC) }
            event.key == Key.Tab && paragraph.kind == ParagraphKind.BULLET -> if (event.isShiftPressed) outdent(
                paragraph
            ) else indent(paragraph)

            (event.key == Key.Enter || event.key == Key.NumPadEnter) && !event.isShiftPressed -> true.also {
                split(
                    paragraph
                )
            }

            event.key == Key.Backspace && collapsed && selection.start == 0 -> mergeWithPrevious(paragraph)
            event.key == Key.Delete && collapsed && selection.start == paragraph.length -> mergeNext(paragraph)
            event.key == Key.DirectionUp && collapsed && selection.start == 0 -> moveFocus(paragraph, -1)
            event.key == Key.DirectionDown && collapsed && selection.start == paragraph.length -> moveFocus(
                paragraph,
                1
            )

            else -> false
        }
    }

    private fun split(paragraph: EditedParagraph) {
        val index = paragraphs.indexOf(paragraph)
        if (paragraph.kind == ParagraphKind.BULLET && paragraph.state.annotatedString.text.isBlank()) {
            if (paragraph.level > 0) outdent(paragraph) else paragraph.kind = ParagraphKind.TEXT
            return
        }
        val cursor = paragraph.state.selection.min
        val before = paragraph.runs(TextRange(0, cursor))
        val after = paragraph.runs(TextRange(paragraph.state.selection.max, paragraph.length))
        paragraph.state.setRuns(before)
        val bullet = paragraph.kind == ParagraphKind.BULLET
        val next =
            create(if (bullet) ParagraphKind.BULLET else ParagraphKind.TEXT, after, if (bullet) paragraph.level else 0)
        paragraphs.add(index + 1, next)
        pendingFocus = FocusTarget(next.id, 0)
    }

    private fun mergeWithPrevious(paragraph: EditedParagraph): Boolean {
        if (paragraph.kind == ParagraphKind.BULLET && paragraph.level > 0) return outdent(paragraph)
        if (paragraph.kind != ParagraphKind.TEXT) {
            paragraph.kind = ParagraphKind.TEXT
            normalize()
            return true
        }
        val index = paragraphs.indexOf(paragraph)
        if (index <= 0) return false
        val previous = paragraphs[index - 1]
        val joint = previous.length
        previous.state.setRuns(previous.runs() + paragraph.runs())
        paragraphs.removeAt(index)
        normalize()
        pendingFocus = FocusTarget(previous.id, joint)
        return true
    }

    private fun mergeNext(paragraph: EditedParagraph): Boolean {
        val index = paragraphs.indexOf(paragraph)
        val next = paragraphs.getOrNull(index + 1) ?: return false
        val joint = paragraph.length
        paragraph.state.setRuns(paragraph.runs() + next.runs())
        paragraphs.removeAt(index + 1)
        normalize()
        pendingFocus = FocusTarget(paragraph.id, joint)
        return true
    }

    private fun moveFocus(paragraph: EditedParagraph, direction: Int): Boolean {
        val target = paragraphs.getOrNull(paragraphs.indexOf(paragraph) + direction) ?: return false
        pendingFocus = FocusTarget(target.id, if (direction < 0) target.length else 0)
        return true
    }

    fun changeKind(paragraph: EditedParagraph, kind: ParagraphKind) {
        paragraph.kind = kind
        normalize()
        menuId = null
        pendingFocus = FocusTarget(paragraph.id, paragraph.length)
    }

    fun startDrag(id: Long) {
        menuId = null
        draggedId = id
        dragOffset = 0f
    }

    fun drag(delta: Float) {
        val id = draggedId ?: return
        dragOffset += delta
        var index = paragraphs.indexOfFirst { it.id == id }
        while (true) {
            val below = paragraphs.getOrNull(index + 1)
            val above = paragraphs.getOrNull(index - 1)
            val belowHeight = below?.let { heights[it.id] } ?: 0
            val aboveHeight = above?.let { heights[it.id] } ?: 0
            when {
                below != null && dragOffset > belowHeight / 2f -> {
                    paragraphs.add(index + 1, paragraphs.removeAt(index))
                    dragOffset -= belowHeight
                    index++
                }

                above != null && dragOffset < -aboveHeight / 2f -> {
                    paragraphs.add(index - 1, paragraphs.removeAt(index))
                    dragOffset += aboveHeight
                    index--
                }

                else -> return
            }
        }
    }

    fun endDrag() {
        draggedId = null
        dragOffset = 0f
        normalize()
    }
}
