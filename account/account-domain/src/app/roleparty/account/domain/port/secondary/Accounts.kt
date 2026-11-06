package app.roleparty.account.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError.EmailAddressAlreadyUsed
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

interface Accounts {

    suspend fun register(account: Account): Outcome<Unit, EmailAddressAlreadyUsed>

    suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress): Outcome<Unit, EmailAddressAlreadyUsed>

    suspend fun changeRole(accountId: AccountId, role: AccountRole)

    suspend fun findById(accountId: AccountId): Account?

    suspend fun findByEmailAddress(emailAddress: EmailAddress): Account?
}
