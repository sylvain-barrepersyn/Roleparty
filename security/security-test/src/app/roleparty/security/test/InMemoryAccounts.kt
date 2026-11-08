package app.roleparty.security.test

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.domain.port.secondary.Accounts

internal class InMemoryAccounts : Accounts {

    private val accounts = mutableMapOf<EmailAddress, AccountId>()

    override suspend fun registerOrFind(emailAddress: EmailAddress) =
        accounts.getOrPut(emailAddress) { AccountId.generate() }

    fun count() = accounts.size
}
