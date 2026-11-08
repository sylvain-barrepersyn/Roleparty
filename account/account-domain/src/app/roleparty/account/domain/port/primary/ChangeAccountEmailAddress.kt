package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

interface ChangeAccountEmailAddress {

    suspend operator fun invoke(accountId: AccountId, emailAddress: EmailAddress): Outcome<Unit, AccountError>
}
