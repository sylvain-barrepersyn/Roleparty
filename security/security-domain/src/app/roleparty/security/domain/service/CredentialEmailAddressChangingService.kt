package app.roleparty.security.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.domain.port.primary.ChangeCredentialEmailAddress
import app.roleparty.security.domain.port.secondary.Credentials

internal class CredentialEmailAddressChangingService(private val credentials: Credentials) : ChangeCredentialEmailAddress {

    override suspend fun invoke(accountId: AccountId, emailAddress: EmailAddress) =
        credentials.changeEmailAddress(accountId, emailAddress)
}
