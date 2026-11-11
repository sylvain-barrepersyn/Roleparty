package app.roleparty.core.document

import kotlinx.serialization.Serializable

@Serializable
enum class ParagraphKind { TEXT, TITLE, SUBTITLE, BULLET }

@Serializable
data class TextRun(val text: String, val bold: Boolean = false, val italic: Boolean = false)

@Serializable
data class Paragraph(val kind: ParagraphKind = ParagraphKind.TEXT, val level: Int = 0, val runs: List<TextRun> = emptyList()) {

    val text get() = runs.joinToString("") { it.text }
}

@Serializable
@JvmInline
value class Document private constructor(val paragraphs: List<Paragraph>) {

    val isBlank get() = paragraphs.all { it.text.isBlank() }

    companion object {
        val EMPTY = Document(emptyList())

        fun from(paragraphs: List<Paragraph>): Document {
            var previous: Paragraph? = null
            val normalized = paragraphs.map { paragraph ->
                paragraph.copy(level = allowedLevel(paragraph.kind, paragraph.level, previous), runs = merged(paragraph.runs)).also { previous = it }
            }
            return if (normalized.all { it.text.isEmpty() }) EMPTY else Document(normalized)
        }

        fun allowedLevel(kind: ParagraphKind, level: Int, previous: Paragraph?): Int = when {
            kind != ParagraphKind.BULLET || previous?.kind != ParagraphKind.BULLET -> 0
            else -> level.coerceIn(0, previous.level + 1)
        }

        private fun merged(runs: List<TextRun>): List<TextRun> =
            runs.filter { it.text.isNotEmpty() }.fold(emptyList()) { acc, run ->
                val last = acc.lastOrNull()
                if (last != null && last.bold == run.bold && last.italic == run.italic) acc.dropLast(1) + last.copy(text = last.text + run.text)
                else acc + run
            }
    }
}
