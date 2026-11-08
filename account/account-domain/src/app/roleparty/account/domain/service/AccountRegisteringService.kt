package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.port.primary.RegisterAccount
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.map

internal class AccountRegisteringService(private val accounts: Accounts) : RegisterAccount {

    override suspend fun invoke(emailAddress: EmailAddress): Outcome<AccountId, AccountError> {
        val account = Account(AccountId.generate(), emailAddress, AccountRole.USER)
        return accounts.register(account).map { account.id }
    }
}
