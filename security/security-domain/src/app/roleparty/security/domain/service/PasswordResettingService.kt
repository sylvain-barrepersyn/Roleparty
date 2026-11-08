package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.EmailCodePurpose.PASSWORD_RESET
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.domain.port.primary.ResetPassword
import app.roleparty.security.domain.port.secondary.Credentials
import app.roleparty.security.domain.port.secondary.SecurityEmails
import java.util.Locale

internal class PasswordResettingService(
    private val credentials: Credentials,
    private val emails: SecurityEmails,
    private val emailThrottlingService: EmailThrottlingService,
    private val emailCodeVerifyingService: EmailCodeVerifyingService,
    private val passwordReplacingService: PasswordReplacingService,
    private val failedAttempts: AttemptLimitingService,
) : ResetPassword {

    override suspend fun request(emailAddress: EmailAddress, locale: Locale) {
        if (credentials.findByEmailAddress(emailAddress) == null || !emailThrottlingService.mayEmail(emailAddress)) return

        val code = emailCodeVerifyingService.create(PASSWORD_RESET, emailAddress, passwordHash = null)
        emails.passwordResetCode(emailAddress, code, locale)
    }

    override suspend fun confirm(emailAddress: EmailAddress, code: String, newPassword: String): Outcome<Unit, SecurityError> {
        if (newPassword.length !in PASSWORD_LENGTH) return Outcome.Failure(SecurityError.InvalidPassword)

        return emailCodeVerifyingService.consume(PASSWORD_RESET, emailAddress, code) {
            val credential = checkNotNull(credentials.findByEmailAddress(emailAddress)) { "A reset code needs a credential" }
            passwordReplacingService.replace(credential.accountId, newPassword)
            failedAttempts.reset(signInAttempts(emailAddress))
        }
    }
}
