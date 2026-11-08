package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountError.AccountNotFound
import app.roleparty.account.domain.model.AccountError.AdminRoleRequired
import app.roleparty.account.domain.model.AccountError.OwnRoleChangeForbidden
import app.roleparty.account.domain.port.primary.ChangeAccountRole
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.core.commons.Outcome

internal class AccountRoleChangingService(private val accounts: Accounts) : ChangeAccountRole {

    override suspend fun invoke(requester: Requester, accountId: AccountId, role: AccountRole): Outcome<Unit, AccountError> {
        if (accounts.findById(requester.accountId)?.isAdmin != true) return Outcome.Failure(AdminRoleRequired)
        if (requester.accountId == accountId) return Outcome.Failure(OwnRoleChangeForbidden)

        val account = accounts.findById(accountId) ?: return Outcome.Failure(AccountNotFound(accountId))
        if (account.role != role) accounts.changeRole(accountId, role)

        return Outcome.success()
    }
}
