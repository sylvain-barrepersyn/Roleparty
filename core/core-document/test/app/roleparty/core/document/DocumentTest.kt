package app.roleparty.core.document

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class DocumentTest : FunSpec({

    test("a bullet should never be deeper than one level under the bullet above it, nor indented after another kind") {
        Document.from(
            listOf(
                Paragraph(ParagraphKind.TEXT, 3, listOf(TextRun("Intro."))),
                Paragraph(ParagraphKind.BULLET, 2, listOf(TextRun("First"))),
                Paragraph(ParagraphKind.BULLET, 5, listOf(TextRun("Second"))),
                Paragraph(ParagraphKind.BULLET, 1, listOf(TextRun("Third"))),
                Paragraph(ParagraphKind.TITLE, 3, listOf(TextRun("Title"))),
            )
        ).paragraphs.map { it.level } shouldBe listOf(0, 0, 1, 1, 0)
    }

    test("runs with the same style should be joined, and empty ones dropped") {
        Document.from(listOf(Paragraph(runs = listOf(TextRun("A "), TextRun(""), TextRun("dark", bold = true), TextRun(" land", bold = true), TextRun(".")))))
            .paragraphs.single().runs shouldBe listOf(TextRun("A "), TextRun("dark land", bold = true), TextRun("."))
    }

    test("a document without any text should be empty") {
        Document.from(listOf(Paragraph(), Paragraph(ParagraphKind.BULLET, runs = listOf(TextRun(""))))) shouldBe Document.EMPTY
        Document.EMPTY.isBlank shouldBe true
    }

    test("a document should be written as a JSON array of paragraphs, and read back the same") {
        val document = Document.from(
            listOf(
                Paragraph(ParagraphKind.TITLE, runs = listOf(TextRun("Barovia"))),
                Paragraph(ParagraphKind.BULLET, runs = listOf(TextRun("A "), TextRun("dark", bold = true, italic = true))),
                Paragraph(ParagraphKind.BULLET, 1, listOf(TextRun("Village"))),
            )
        )
        val json = Json { encodeDefaults = true }
        val written = json.encodeToString(document)

        written shouldBe """[{"kind":"TITLE","level":0,"runs":[{"text":"Barovia","bold":false,"italic":false}]},""" +
            """{"kind":"BULLET","level":0,"runs":[{"text":"A ","bold":false,"italic":false},{"text":"dark","bold":true,"italic":true}]},""" +
            """{"kind":"BULLET","level":1,"runs":[{"text":"Village","bold":false,"italic":false}]}]"""
        json.decodeFromString<Document>(written) shouldBe document
    }
})
