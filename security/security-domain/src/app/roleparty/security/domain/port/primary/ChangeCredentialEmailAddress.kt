package app.roleparty.security.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress

interface ChangeCredentialEmailAddress {

    suspend operator fun invoke(accountId: AccountId, emailAddress: EmailAddress)
}
