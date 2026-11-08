package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.Credential
import app.roleparty.security.domain.model.EmailCodePurpose.SIGN_UP
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.domain.port.primary.VerifyEmailAddress
import app.roleparty.security.domain.port.secondary.Accounts
import app.roleparty.security.domain.port.secondary.Credentials

internal class EmailAddressVerifyingService(
    private val accounts: Accounts,
    private val credentials: Credentials,
    private val emailCodeVerifyingService: EmailCodeVerifyingService,
) : VerifyEmailAddress {

    override suspend fun invoke(emailAddress: EmailAddress, code: String): Outcome<Unit, SecurityError> =
        emailCodeVerifyingService.consume(SIGN_UP, emailAddress, code) { emailCode ->
            val accountId = accounts.registerOrFind(emailAddress)
            val passwordHash = checkNotNull(emailCode.passwordHash) { "A sign up code must hold a password" }
            credentials.saveIfAbsent(Credential(accountId, emailAddress, passwordHash))
        }
}
