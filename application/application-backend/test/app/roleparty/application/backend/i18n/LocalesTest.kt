package app.roleparty.application.backend.i18n

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale

class LocalesTest {

    @Test
    fun `should resolve French when preferred`() {
        assertEquals(Locale.FRENCH, Locales.resolve("fr-FR,fr;q=0.9,en;q=0.8"))
    }

    @Test
    fun `should resolve English when preferred`() {
        assertEquals(Locale.ENGLISH, Locales.resolve("en-US,fr;q=0.5"))
    }

    @Test
    fun `should fall back to English for unsupported, missing or malformed headers`() {
        assertEquals(Locale.ENGLISH, Locales.resolve("de-DE"))
        assertEquals(Locale.ENGLISH, Locales.resolve(null))
        assertEquals(Locale.ENGLISH, Locales.resolve(";;;"))
    }
}
