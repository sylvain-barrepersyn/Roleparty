package app.roleparty.account.test

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.AccountModule
import app.roleparty.account.domain.model.Account
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull

class AccountFixtures {

    private val store = InMemoryAccounts()
    private var generatedEmailAddresses = 0

    val useCases = AccountModule(store).useCases

    suspend fun `given an account`(emailAddress: EmailAddress = generateEmailAddress()): AccountId =
        useCases.register(emailAddress).shouldBeSuccess()

    suspend fun `given an admin`(emailAddress: EmailAddress = generateEmailAddress()): AccountId {
        val accountId = `given an account`(emailAddress)
        store.changeRole(accountId, AccountRole.ADMIN)
        return accountId
    }

    suspend fun accountOf(accountId: AccountId): Account = store.findById(accountId).shouldNotBeNull()

    fun writes(): Int = store.writes

    private fun generateEmailAddress() = EmailAddress.from("account-${++generatedEmailAddresses}@roleparty.app")
}
