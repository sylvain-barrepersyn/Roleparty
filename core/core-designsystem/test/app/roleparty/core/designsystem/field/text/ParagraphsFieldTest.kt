package app.roleparty.core.designsystem.field.text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import app.roleparty.core.designsystem.surface.BaseSurface
import app.roleparty.core.designsystem.theme.RolepartyTheme
import app.roleparty.core.document.Document
import app.roleparty.core.document.Paragraph
import app.roleparty.core.document.ParagraphKind
import app.roleparty.core.document.TextRun
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

@OptIn(ExperimentalTestApi::class)
class ParagraphsFieldTest : FunSpec({

    val defaultLocale = Locale.getDefault()
    beforeTest { Locale.setDefault(Locale.ENGLISH) }
    afterTest { Locale.setDefault(defaultLocale) }

    fun ComposeUiTest.field(initial: String): () -> String {
        var document by mutableStateOf(outlined(initial))
        setContent {
            RolepartyTheme {
                BaseSurface {
                    ParagraphsField(document = document, onDocumentChange = { document = it }, structured = true)
                }
            }
        }
        waitForIdle()
        return { outline(document) }
    }

    fun ComposeUiTest.paragraph(index: Int) = onAllNodes(hasSetTextAction())[index]

    fun ComposeUiTest.press(index: Int, keys: List<Key>) {
        paragraph(index).performKeyInput { keys.forEach { pressKey(it) } }
        waitForIdle()
    }

    test("enter after a filled bullet should start another bullet, and enter on an empty bullet should turn it back into text") {
        runComposeUiTest {
            val content = field("- Ireena")
            paragraph(0).performClick()
            press(0, listOf(Key.MoveEnd, Key.Enter))
            paragraph(1).performTextInput("Ismark")
            waitForIdle()
            content() shouldBe "- Ireena\n- Ismark"

            press(1, listOf(Key.MoveEnd, Key.Enter))
            press(2, listOf(Key.Enter))
            paragraph(2).performTextInput("The end.")
            waitForIdle()
            content() shouldBe "- Ireena\n- Ismark\nThe end."
        }
    }

    test("backspace at the start of a title should turn it into text, then join it to the paragraph above") {
        runComposeUiTest {
            val content = field("Intro.\n# Barovia")
            paragraph(1).performClick()
            press(1, listOf(Key.MoveHome, Key.Backspace))
            content() shouldBe "Intro.\nBarovia"

            press(1, listOf(Key.MoveHome, Key.Backspace))
            content() shouldBe "Intro.Barovia"
        }
    }

    test("enter in the middle of a paragraph should split it, keeping the bold of each part") {
        runComposeUiTest {
            val content = field("A **dark** land")
            paragraph(0).performClick()
            press(0, listOf(Key.MoveHome, Key.DirectionRight, Key.DirectionRight, Key.DirectionRight, Key.DirectionRight, Key.Enter))
            content() shouldBe "A **da**\n**rk** land"
        }
    }

    test("the handle should let choose the kind of a paragraph") {
        runComposeUiTest {
            val content = field("Barovia\nIreena")
            onAllNodesWithContentDescription("Drag to move, click to change the type")[0].performClick()
            waitForIdle()
            onNodeWithText("Title").performClick()
            waitForIdle()
            content() shouldBe "# Barovia\nIreena"
        }
    }

    test("dragging a paragraph by its handle should move it below the next one") {
        runComposeUiTest {
            val content = field("First.\nSecond.\nThird.")
            onAllNodesWithContentDescription("Drag to move, click to change the type")[0].performMouseInput {
                moveTo(center)
                press()
                repeat(4) { moveBy(Offset(0f, 5f)) }
                release()
            }
            waitForIdle()
            content() shouldBe "Second.\nFirst.\nThird."
        }
    }

    test("tab should nest a bullet under the one above, as deep as wanted, enter keeping the level and shift tab bringing it back") {
        runComposeUiTest {
            val content = field("- Barovia")
            paragraph(0).performClick()
            press(0, listOf(Key.MoveEnd, Key.Enter, Key.Tab))
            paragraph(1).performTextInput("Village")
            press(1, listOf(Key.MoveEnd, Key.Enter, Key.Tab))
            paragraph(2).performTextInput("Inn")
            press(2, listOf(Key.MoveEnd, Key.Enter))
            paragraph(3).performTextInput("Church")
            waitForIdle()
            content() shouldBe "- Barovia\n  - Village\n    - Inn\n    - Church"

            paragraph(3).performKeyInput { keyDown(Key.ShiftLeft); pressKey(Key.Tab); keyUp(Key.ShiftLeft) }
            waitForIdle()
            content() shouldBe "- Barovia\n  - Village\n    - Inn\n  - Church"
        }
    }

    test("a bullet should not be nested deeper than one level under the bullet above it") {
        runComposeUiTest {
            val content = field("- Barovia\n- Vallaki")
            paragraph(1).performClick()
            press(1, listOf(Key.Tab, Key.Tab, Key.Tab))
            content() shouldBe "- Barovia\n  - Vallaki"
        }
    }

    test("enter on an empty sub-item should bring it up one level before turning it into text") {
        runComposeUiTest {
            val content = field("- Barovia\n  - Village")
            paragraph(1).performClick()
            press(1, listOf(Key.MoveEnd, Key.Enter, Key.Enter))
            paragraph(2).performTextInput("Vallaki")
            waitForIdle()
            content() shouldBe "- Barovia\n  - Village\n- Vallaki"
        }
    }

    test("nesting an item should move its own sub-items along") {
        runComposeUiTest {
            val content = field("- Barovia\n- Village\n  - Inn")
            paragraph(1).performClick()
            press(1, listOf(Key.Tab))
            content() shouldBe "- Barovia\n  - Village\n    - Inn"
        }
    }
})

private val inline = Regex("""\*\*([^*]+)\*\*|\*([^*]+)\*|([^*]+)""")

private fun outlined(outline: String): Document = Document.from(
    outline.lines().filter { it.isNotBlank() }.map { raw ->
        val level = raw.takeWhile { it == ' ' }.length / 2
        val line = raw.trim()
        val kind = when {
            line.startsWith("## ") -> ParagraphKind.SUBTITLE
            line.startsWith("# ") -> ParagraphKind.TITLE
            line.startsWith("- ") -> ParagraphKind.BULLET
            else -> ParagraphKind.TEXT
        }
        val text = line.removePrefix("## ").removePrefix("# ").removePrefix("- ")
        Paragraph(kind, level, inline.findAll(text).map { match ->
            val groups = match.groupValues
            when {
                groups[1].isNotEmpty() -> TextRun(groups[1], bold = true)
                groups[2].isNotEmpty() -> TextRun(groups[2], italic = true)
                else -> TextRun(groups[3])
            }
        }.toList())
    }
)

private fun outline(document: Document): String = document.paragraphs.joinToString("\n") { paragraph ->
    val prefix = when (paragraph.kind) {
        ParagraphKind.TEXT -> ""
        ParagraphKind.TITLE -> "# "
        ParagraphKind.SUBTITLE -> "## "
        ParagraphKind.BULLET -> "- "
    }
    "  ".repeat(paragraph.level) + prefix + paragraph.runs.joinToString("") { run ->
        when {
            run.bold -> "**${run.text}**"
            run.italic -> "*${run.text}*"
            else -> run.text
        }
    }
}
