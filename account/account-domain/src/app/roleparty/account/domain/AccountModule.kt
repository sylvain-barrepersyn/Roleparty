package app.roleparty.account.domain

import app.roleparty.account.domain.port.primary.ChangeAccountEmailAddress
import app.roleparty.account.domain.port.primary.ChangeAccountRole
import app.roleparty.account.domain.port.primary.FindAccount
import app.roleparty.account.domain.port.primary.RegisterAccount
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.account.domain.service.AccountEmailAddressChangingService
import app.roleparty.account.domain.service.AccountFindingService
import app.roleparty.account.domain.service.AccountRegisteringService
import app.roleparty.account.domain.service.AccountRoleChangingService

class AccountModule(accounts: Accounts) {

    val registerAccount: RegisterAccount = AccountRegisteringService(accounts)

    val changeAccountEmailAddress: ChangeAccountEmailAddress = AccountEmailAddressChangingService(accounts)

    val changeAccountRole: ChangeAccountRole = AccountRoleChangingService(accounts)

    val findAccount: FindAccount = AccountFindingService(accounts)
}
