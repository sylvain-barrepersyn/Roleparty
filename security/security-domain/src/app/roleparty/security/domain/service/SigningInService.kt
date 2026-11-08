package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.Authentication
import app.roleparty.security.domain.model.DeviceId
import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.domain.port.primary.SignIn
import app.roleparty.security.domain.port.secondary.Credentials

internal class SigningInService(
    private val credentials: Credentials,
    private val passwordHashingService: PasswordHashingService,
    private val failedAttempts: AttemptLimitingService,
    private val authenticationIssuingService: AuthenticationIssuingService,
    private val dummyPasswordHash: PasswordHash,
) : SignIn {

    override suspend fun invoke(emailAddress: EmailAddress, password: String): Outcome<Authentication, SecurityError> {
        val attempts = signInAttempts(emailAddress)
        if (failedAttempts.isBlocked(attempts)) return Outcome.Failure(SecurityError.TooManyAttempts)
        if (password.length > PASSWORD_LENGTH.last) return Outcome.Failure(SecurityError.InvalidCredentials)

        val credential = credentials.findByEmailAddress(emailAddress)
        val verified = passwordHashingService.verify(password, credential?.passwordHash ?: dummyPasswordHash)

        if (credential == null || !verified) {
            failedAttempts.record(attempts)
            return Outcome.Failure(SecurityError.InvalidCredentials)
        }

        failedAttempts.reset(attempts)
        return Outcome.Success(authenticationIssuingService.issue(credential.accountId, DeviceId.generate()))
    }
}
