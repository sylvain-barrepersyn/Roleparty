package app.roleparty.application.backend.i18n

import java.text.MessageFormat
import java.util.Locale
import java.util.ResourceBundle

class Messages(private val bundle: String) {

    private val control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)

    fun get(key: String, locale: Locale, vararg arguments: Any): String {
        val pattern = ResourceBundle.getBundle("i18n.$bundle", locale, control).getString(key)
        return MessageFormat(pattern, locale).format(arguments)
    }

    companion object {
        val ERRORS = Messages("errors")
    }
}
