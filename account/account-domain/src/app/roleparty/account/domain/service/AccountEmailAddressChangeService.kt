package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountError.EmailAddressAlreadyUsed
import app.roleparty.account.domain.model.AccountLifecycleCommand.ChangeEmailAddress
import app.roleparty.account.domain.port.primary.AccountEmailAddressChangeCommand
import app.roleparty.account.domain.port.primary.AccountEmailAddressChangeCommandHandler
import app.roleparty.account.domain.port.primary.AccountOverviewQuery
import app.roleparty.account.domain.port.primary.AccountOverviewQuery.Filter
import app.roleparty.account.domain.port.primary.AccountOverviewQueryHandler
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.core.commons.Outcome
import kotlin.time.Clock

internal class AccountEmailAddressChangeService(
    private val accounts: Accounts,
    private val accountOverviewQueryHandler: AccountOverviewQueryHandler,
    private val clock: Clock,
) : AccountEmailAddressChangeCommandHandler {

    override suspend fun handle(accountId: AccountId, command: AccountEmailAddressChangeCommand): Outcome<Unit, AccountError> {
        val owner = accountOverviewQueryHandler.handle(
            query = AccountOverviewQuery(filter = Filter.ByEmailAddress(command.emailAddress))
        )
        if (owner != null && owner.accountId != accountId) {
            return Outcome.Failure(EmailAddressAlreadyUsed(command.emailAddress))
        }

        return accounts.decideAndAppend(accountId, ChangeEmailAddress(command.emailAddress), clock)
    }
}
