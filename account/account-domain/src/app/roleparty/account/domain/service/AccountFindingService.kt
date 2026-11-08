package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.port.primary.FindAccount
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress

internal class AccountFindingService(private val accounts: Accounts) : FindAccount {

    override suspend fun byId(accountId: AccountId) = accounts.findById(accountId)

    override suspend fun byEmailAddress(emailAddress: EmailAddress) = accounts.findByEmailAddress(emailAddress)
}
