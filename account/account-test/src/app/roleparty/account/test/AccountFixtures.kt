package app.roleparty.account.test

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.AccountModule
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.account.domain.port.primary.AccountEmailAddressChangeCommand
import app.roleparty.account.domain.port.primary.AccountRegistrationCommand
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.ControlledClock
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull

class AccountFixtures(val clock: ControlledClock = ControlledClock()) {

    private val store = InMemoryAccounts()
    private val accounts = RacingAccounts(store, clock)
    private val accountOverviews = RacingAccountOverviews(store)
    private val accountModule = AccountModule(accounts = accounts, accountOverviews = accountOverviews, clock = clock)
    private var generatedEmailAddresses = 0

    val accountOverviewQueryHandler = accountModule.accountOverviewQueryHandler
    val accountRegistrationCommandHandler = accountModule.accountRegistrationCommandHandler
    val accountEmailAddressChangeCommandHandler = accountModule.accountEmailAddressChangeCommandHandler
    val accountRoleChangeCommandHandler = accountModule.accountRoleChangeCommandHandler

    suspend fun `given an account`(emailAddress: EmailAddress = generateEmailAddress()): AccountId =
        accountRegistrationCommandHandler.handle(AccountRegistrationCommand(emailAddress)).shouldBeSuccess()

    suspend fun `given an admin`(emailAddress: EmailAddress = generateEmailAddress()): AccountId {
        val accountId = `given an account`(emailAddress)
        val promotion = AccountEvent(
            id = AccountEvent.Id.generate(),
            accountId = accountId,
            occurredAt = clock.now(),
            payload = Payload.RoleChanged(AccountRole.ADMIN),
        )
        store.append(accountId, expectedVersion = store.load(accountId).version, events = listOf(promotion)).shouldBeSuccess()
        return accountId
    }

    suspend fun `given an email address change`(accountId: AccountId, emailAddress: EmailAddress) {
        accountEmailAddressChangeCommandHandler.handle(accountId, AccountEmailAddressChangeCommand(emailAddress)).shouldBeSuccess()
    }

    fun `given an email check missing concurrent writes`() {
        accountOverviews.emailCheckMissesConcurrentWrites = true
    }

    fun `given concurrent writes`(vararg payloads: Payload) {
        accounts.enqueue(payloads.toList())
    }

    suspend fun eventsOf(accountId: AccountId): List<AccountEvent> = store.load(accountId).events

    suspend fun overviewOf(accountId: AccountId): AccountOverview = store.findById(accountId).shouldNotBeNull()

    private fun generateEmailAddress() = EmailAddress.from("account-${++generatedEmailAddresses}@roleparty.app")
}
