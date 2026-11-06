package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

data class AccountEmailAddressChangeCommand(val emailAddress: EmailAddress)

interface AccountEmailAddressChangeCommandHandler {

    suspend fun handle(accountId: AccountId, command: AccountEmailAddressChangeCommand): Outcome<Unit, AccountError>
}
