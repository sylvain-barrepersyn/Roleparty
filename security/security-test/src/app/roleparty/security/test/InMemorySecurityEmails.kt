package app.roleparty.security.test

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.domain.port.secondary.SecurityEmails
import java.util.Locale

sealed interface SentEmail {
    val to: EmailAddress

    data class Verification(override val to: EmailAddress, val code: String, val locale: Locale) : SentEmail
    data class AlreadyRegistered(override val to: EmailAddress, val locale: Locale) : SentEmail
    data class PasswordReset(override val to: EmailAddress, val code: String, val locale: Locale) : SentEmail
}

internal class InMemorySecurityEmails : SecurityEmails {

    val sent = mutableListOf<SentEmail>()

    override fun verificationCode(to: EmailAddress, code: String, locale: Locale) {
        sent += SentEmail.Verification(to, code, locale)
    }

    override fun alreadyRegistered(to: EmailAddress, locale: Locale) {
        sent += SentEmail.AlreadyRegistered(to, locale)
    }

    override fun passwordResetCode(to: EmailAddress, code: String, locale: Locale) {
        sent += SentEmail.PasswordReset(to, code, locale)
    }
}
