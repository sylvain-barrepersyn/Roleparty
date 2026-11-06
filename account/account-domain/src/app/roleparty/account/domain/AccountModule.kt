package app.roleparty.account.domain

import app.roleparty.account.domain.port.primary.AccountEmailAddressChangeCommandHandler
import app.roleparty.account.domain.port.primary.AccountOverviewQueryHandler
import app.roleparty.account.domain.port.primary.AccountRegistrationCommandHandler
import app.roleparty.account.domain.port.primary.AccountRoleChangeCommandHandler
import app.roleparty.account.domain.port.secondary.AccountOverviews
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.account.domain.service.AccountEmailAddressChangeService
import app.roleparty.account.domain.service.AccountOverviewQueryService
import app.roleparty.account.domain.service.AccountRegistrationService
import app.roleparty.account.domain.service.AccountRoleChangeService
import kotlin.time.Clock

class AccountModule(
    accounts: Accounts,
    accountOverviews: AccountOverviews,
    clock: Clock = Clock.System,
) {
    val accountOverviewQueryHandler: AccountOverviewQueryHandler =
        AccountOverviewQueryService(accountOverviews)

    val accountRegistrationCommandHandler: AccountRegistrationCommandHandler =
        AccountRegistrationService(accounts, accountOverviewQueryHandler, clock)

    val accountEmailAddressChangeCommandHandler: AccountEmailAddressChangeCommandHandler =
        AccountEmailAddressChangeService(accounts, accountOverviewQueryHandler, clock)

    val accountRoleChangeCommandHandler: AccountRoleChangeCommandHandler =
        AccountRoleChangeService(accounts, accountOverviewQueryHandler, clock)
}
