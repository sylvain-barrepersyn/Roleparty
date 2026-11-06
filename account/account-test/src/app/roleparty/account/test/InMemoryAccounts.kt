package app.roleparty.account.test

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError.EmailAddressAlreadyUsed
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

class InMemoryAccounts : Accounts {

    private val accounts = mutableMapOf<AccountId, Account>()

    var writes = 0
        private set

    override suspend fun register(account: Account) =
        if (isUsedByAnother(account.emailAddress, account.id)) Outcome.Failure(EmailAddressAlreadyUsed(account.emailAddress))
        else Outcome.Success(save(account))

    override suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress) =
        if (isUsedByAnother(emailAddress, accountId)) Outcome.Failure(EmailAddressAlreadyUsed(emailAddress))
        else Outcome.Success(update(accountId) { it.copy(emailAddress = emailAddress) })

    override suspend fun changeRole(accountId: AccountId, role: AccountRole) =
        update(accountId) { it.copy(role = role) }

    override suspend fun findById(accountId: AccountId) = accounts[accountId]

    override suspend fun findByEmailAddress(emailAddress: EmailAddress) =
        accounts.values.find { it.emailAddress == emailAddress }

    private fun isUsedByAnother(emailAddress: EmailAddress, accountId: AccountId) =
        accounts.values.any { it.id != accountId && it.emailAddress == emailAddress }

    private fun update(accountId: AccountId, change: (Account) -> Account) {
        accounts[accountId]?.let { save(change(it)) }
    }

    private fun save(account: Account) {
        accounts[account.id] = account
        writes++
    }
}
