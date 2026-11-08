package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.EmailCodePurpose.SIGN_UP
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.domain.port.primary.SignUp
import app.roleparty.security.domain.port.secondary.Credentials
import app.roleparty.security.domain.port.secondary.SecurityEmails
import java.util.Locale

internal class SigningUpService(
    private val credentials: Credentials,
    private val passwordHashingService: PasswordHashingService,
    private val emails: SecurityEmails,
    private val emailThrottlingService: EmailThrottlingService,
    private val emailCodeVerifyingService: EmailCodeVerifyingService,
) : SignUp {

    override suspend fun invoke(emailAddress: EmailAddress, password: String, locale: Locale): Outcome<Unit, SecurityError> {
        if (password.length !in PASSWORD_LENGTH) return Outcome.Failure(SecurityError.InvalidPassword)

        val passwordHash = passwordHashingService.hash(password)
        if (!emailThrottlingService.mayEmail(emailAddress)) return Outcome.success()

        if (credentials.findByEmailAddress(emailAddress) != null) {
            emails.alreadyRegistered(emailAddress, locale)
        } else {
            val code = emailCodeVerifyingService.create(SIGN_UP, emailAddress, passwordHash)
            emails.verificationCode(emailAddress, code, locale)
        }
        return Outcome.success()
    }
}
