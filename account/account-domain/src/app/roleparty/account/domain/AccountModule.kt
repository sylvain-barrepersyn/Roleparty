package app.roleparty.account.domain

import app.roleparty.account.domain.port.primary.AccountUseCases
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.account.domain.service.AccountService

class AccountModule(accounts: Accounts) {

    val useCases: AccountUseCases = AccountService(accounts)
}
