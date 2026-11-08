package app.roleparty.security.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.domain.port.primary.ChangePassword
import app.roleparty.security.domain.port.secondary.Credentials

internal class PasswordChangingService(
    private val credentials: Credentials,
    private val passwordHashingService: PasswordHashingService,
    private val failedAttempts: AttemptLimitingService,
    private val passwordReplacingService: PasswordReplacingService,
) : ChangePassword {

    override suspend fun invoke(accountId: AccountId, currentPassword: String, newPassword: String): Outcome<Unit, SecurityError> {
        if (newPassword.length !in PASSWORD_LENGTH) return Outcome.Failure(SecurityError.InvalidPassword)

        val attempts = "change-password:$accountId"
        if (failedAttempts.isBlocked(attempts)) return Outcome.Failure(SecurityError.TooManyAttempts)

        val credential = credentials.findByAccountId(accountId)
        val verified = currentPassword.length <= PASSWORD_LENGTH.last && credential != null &&
            passwordHashingService.verify(currentPassword, credential.passwordHash)
        if (!verified) {
            failedAttempts.record(attempts)
            return Outcome.Failure(SecurityError.InvalidCredentials)
        }

        failedAttempts.reset(attempts)
        passwordReplacingService.replace(accountId, newPassword)
        return Outcome.success()
    }
}
