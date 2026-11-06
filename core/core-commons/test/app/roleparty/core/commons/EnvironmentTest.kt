package app.roleparty.core.commons

import java.nio.file.Path
import kotlin.io.path.writeText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

class EnvironmentTest {

    @Test
    fun `should prefer the trimmed content of the file`(@TempDir directory: Path) {
        // Given
        val secret = directory.resolve("secret").apply { writeText("from-file\n") }

        // When
        val value = mapOf("NAME" to "plain", "NAME_FILE" to secret.toString()).valueOrFile("NAME")

        // Then
        assertEquals("from-file", value)
    }

    @Test
    fun `should use the plain value when the file is not readable`() {
        // When
        val value = mapOf("NAME" to "plain", "NAME_FILE" to "/does/not/exist").valueOrFile("NAME")

        // Then
        assertEquals("plain", value)
    }

    @Test
    fun `should have no value when nothing is set`() {
        assertNull(emptyMap<String, String>().valueOrFile("NAME"))
    }

    @Test
    fun `should fail when a required value is not set`() {
        assertThrows<IllegalStateException> { emptyMap<String, String>().requireValueOrFile("NAME") }
    }
}
