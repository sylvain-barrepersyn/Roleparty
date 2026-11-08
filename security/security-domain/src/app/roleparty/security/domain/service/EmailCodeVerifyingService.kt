package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.EmailCode
import app.roleparty.security.domain.model.EmailCodePurpose
import app.roleparty.security.domain.model.EmailCodePurpose.SIGN_UP
import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.model.SecretGenerator
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.domain.port.secondary.EmailCodes
import kotlin.time.Clock

internal class EmailCodeVerifyingService(
    private val emailCodes: EmailCodes,
    private val failedAttempts: AttemptLimitingService,
    private val clock: Clock,
) {

    suspend fun create(purpose: EmailCodePurpose, emailAddress: EmailAddress, passwordHash: PasswordHash?): String {
        val code = SecretGenerator.emailCode()
        val duration = if (purpose == SIGN_UP) SIGN_UP_CODE_DURATION else PASSWORD_RESET_CODE_DURATION
        emailCodes.save(EmailCode(SecretHash.of(code), purpose, emailAddress, passwordHash, clock.now() + duration))
        return code
    }

    suspend fun consume(
        purpose: EmailCodePurpose,
        emailAddress: EmailAddress,
        code: String,
        action: suspend (EmailCode) -> Unit,
    ): Outcome<Unit, SecurityError> {
        val attempts = "$purpose:$emailAddress"
        if (failedAttempts.isBlocked(attempts)) return Outcome.Failure(SecurityError.TooManyAttempts)

        val emailCode = emailCodes.find(SecretHash.of(code.trim().uppercase()), purpose, emailAddress)
            ?.takeIf { it.expiration > clock.now() }
        if (emailCode == null) {
            failedAttempts.record(attempts)
            return Outcome.Failure(SecurityError.InvalidEmailCode)
        }

        action(emailCode)
        emailCodes.deleteAll(purpose, emailAddress)
        failedAttempts.reset(attempts)
        return Outcome.success()
    }
}
