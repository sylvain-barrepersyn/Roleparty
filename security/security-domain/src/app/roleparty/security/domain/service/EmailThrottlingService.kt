package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import kotlin.time.Clock

internal class EmailThrottlingService(clock: Clock) {

    private val sentEmails = AttemptLimitingService(MAX_EMAILS, EMAILS_WINDOW, clock)

    fun mayEmail(emailAddress: EmailAddress): Boolean {
        val key = emailAddress.toString()
        if (sentEmails.isBlocked(key)) return false
        sentEmails.record(key)
        return true
    }
}
