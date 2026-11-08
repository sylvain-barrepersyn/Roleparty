package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

interface RegisterAccount {

    suspend operator fun invoke(emailAddress: EmailAddress): Outcome<AccountId, AccountError>
}
