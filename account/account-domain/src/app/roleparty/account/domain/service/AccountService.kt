package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountError.AccountNotFound
import app.roleparty.account.domain.model.AccountError.AdminRoleRequired
import app.roleparty.account.domain.model.AccountError.OwnRoleChangeForbidden
import app.roleparty.account.domain.port.primary.AccountUseCases
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.map

internal class AccountService(private val accounts: Accounts) : AccountUseCases {

    override suspend fun register(emailAddress: EmailAddress): Outcome<AccountId, AccountError> {
        val account = Account(AccountId.generate(), emailAddress, AccountRole.USER)
        return accounts.register(account).map { account.id }
    }

    override suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress): Outcome<Unit, AccountError> {
        val account = accounts.findById(accountId) ?: return Outcome.Failure(AccountNotFound(accountId))
        if (account.emailAddress == emailAddress) return Outcome.success()

        return accounts.changeEmailAddress(accountId, emailAddress)
    }

    override suspend fun changeRole(requester: Requester, accountId: AccountId, role: AccountRole): Outcome<Unit, AccountError> {
        if (accounts.findById(requester.accountId)?.isAdmin != true) return Outcome.Failure(AdminRoleRequired)
        if (requester.accountId == accountId) return Outcome.Failure(OwnRoleChangeForbidden)

        val account = accounts.findById(accountId) ?: return Outcome.Failure(AccountNotFound(accountId))
        if (account.role != role) accounts.changeRole(accountId, role)

        return Outcome.success()
    }

    override suspend fun findById(accountId: AccountId) = accounts.findById(accountId)

    override suspend fun findByEmailAddress(emailAddress: EmailAddress) = accounts.findByEmailAddress(emailAddress)
}
