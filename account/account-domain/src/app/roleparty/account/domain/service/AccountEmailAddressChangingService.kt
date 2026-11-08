package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountError.AccountNotFound
import app.roleparty.account.domain.port.primary.ChangeAccountEmailAddress
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

internal class AccountEmailAddressChangingService(private val accounts: Accounts) : ChangeAccountEmailAddress {

    override suspend fun invoke(accountId: AccountId, emailAddress: EmailAddress): Outcome<Unit, AccountError> {
        val account = accounts.findById(accountId) ?: return Outcome.Failure(AccountNotFound(accountId))
        if (account.emailAddress == emailAddress) return Outcome.success()

        return accounts.changeEmailAddress(accountId, emailAddress)
    }
}
