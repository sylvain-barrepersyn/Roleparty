package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountError.AdminRoleRequired
import app.roleparty.account.domain.model.AccountError.OwnRoleChangeForbidden
import app.roleparty.account.domain.model.AccountLifecycleCommand.ChangeRole
import app.roleparty.account.domain.port.primary.AccountOverviewQuery
import app.roleparty.account.domain.port.primary.AccountOverviewQuery.Filter
import app.roleparty.account.domain.port.primary.AccountOverviewQueryHandler
import app.roleparty.account.domain.port.primary.AccountRoleChangeCommand
import app.roleparty.account.domain.port.primary.AccountRoleChangeCommandHandler
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.core.commons.Outcome
import kotlin.time.Clock

internal class AccountRoleChangeService(
    private val accounts: Accounts,
    private val accountOverviewQueryHandler: AccountOverviewQueryHandler,
    private val clock: Clock,
) : AccountRoleChangeCommandHandler {

    override suspend fun handle(accountId: AccountId, command: AccountRoleChangeCommand): Outcome<Unit, AccountError> {
        val requesterOverview = accountOverviewQueryHandler.handle(
            query = AccountOverviewQuery(filter = Filter.ByAccountId(command.requester.accountId))
        )
        if (requesterOverview?.isAdmin != true) return Outcome.Failure(AdminRoleRequired)
        if (command.requester.accountId == accountId) return Outcome.Failure(OwnRoleChangeForbidden)

        return accounts.decideAndAppend(accountId, ChangeRole(command.role), clock)
    }
}
