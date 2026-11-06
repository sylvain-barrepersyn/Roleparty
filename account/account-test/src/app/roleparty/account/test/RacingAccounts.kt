package app.roleparty.account.test

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountVersion
import app.roleparty.account.domain.port.secondary.AccountAppendFailure
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.core.commons.Outcome
import app.roleparty.core.test.shouldBeSuccess
import kotlin.time.Clock

internal class RacingAccounts(
    private val delegate: Accounts,
    private val clock: Clock,
) : Accounts by delegate {

    private val pendingWrites = ArrayDeque<AccountEvent.Payload>()

    fun enqueue(concurrentWrites: List<AccountEvent.Payload>) {
        pendingWrites.addAll(concurrentWrites)
    }

    override suspend fun append(
        accountId: AccountId,
        expectedVersion: AccountVersion,
        events: List<AccountEvent>,
    ): Outcome<Unit, AccountAppendFailure> {
        pendingWrites.removeFirstOrNull()?.let { payload ->
            val event = AccountEvent(AccountEvent.Id.generate(), accountId, clock.now(), payload)
            delegate.append(accountId, delegate.load(accountId).version, listOf(event)).shouldBeSuccess()
        }
        return delegate.append(accountId, expectedVersion, events)
    }
}
