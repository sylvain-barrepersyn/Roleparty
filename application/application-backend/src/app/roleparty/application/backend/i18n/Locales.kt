package app.roleparty.application.backend.i18n

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.acceptLanguage
import java.util.Locale

object Locales {
    val DEFAULT: Locale = Locale.ENGLISH
    val SUPPORTED: List<Locale> = listOf(Locale.ENGLISH, Locale.FRENCH)

    fun resolve(acceptLanguage: String?): Locale {
        if (acceptLanguage.isNullOrBlank()) return DEFAULT

        val ranges = try {
            Locale.LanguageRange.parse(acceptLanguage)
        } catch (_: IllegalArgumentException) {
            return DEFAULT
        }
        return Locale.lookup(ranges, SUPPORTED) ?: DEFAULT
    }
}

val ApplicationCall.locale: Locale get() = Locales.resolve(request.acceptLanguage())
