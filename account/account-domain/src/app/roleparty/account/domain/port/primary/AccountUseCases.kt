package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

interface AccountUseCases {

    suspend fun register(emailAddress: EmailAddress): Outcome<AccountId, AccountError>

    suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress): Outcome<Unit, AccountError>

    suspend fun changeRole(requester: Requester, accountId: AccountId, role: AccountRole): Outcome<Unit, AccountError>

    suspend fun findById(accountId: AccountId): Account?

    suspend fun findByEmailAddress(emailAddress: EmailAddress): Account?
}
