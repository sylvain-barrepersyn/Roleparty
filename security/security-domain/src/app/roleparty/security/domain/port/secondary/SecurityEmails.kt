package app.roleparty.security.domain.port.secondary

import app.roleparty.communication.commons.EmailAddress
import java.util.Locale

interface SecurityEmails {

    fun verificationCode(to: EmailAddress, code: String, locale: Locale)

    fun alreadyRegistered(to: EmailAddress, locale: Locale)

    fun passwordResetCode(to: EmailAddress, code: String, locale: Locale)
}
