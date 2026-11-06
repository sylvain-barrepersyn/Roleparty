package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountDecider
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountError.AccountNotFound
import app.roleparty.account.domain.model.AccountError.ConcurrentModification
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountLifecycleCommand
import app.roleparty.account.domain.port.secondary.AccountAppendFailure
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.core.commons.Outcome
import kotlin.time.Clock

private const val MAX_APPEND_ATTEMPTS = 3

internal fun List<AccountEvent.Payload>.stamp(accountId: AccountId, clock: Clock): List<AccountEvent> {
    val occurredAt = clock.now()
    return map { payload ->
        AccountEvent(id = AccountEvent.Id.generate(), accountId = accountId, occurredAt = occurredAt, payload = payload)
    }
}

internal suspend fun Accounts.decideAndAppend(
    accountId: AccountId,
    command: AccountLifecycleCommand,
    clock: Clock,
): Outcome<Unit, AccountError> {
    repeat(MAX_APPEND_ATTEMPTS) {
        val (events, version) = load(accountId)
        if (events.isEmpty()) return Outcome.Failure(AccountNotFound(accountId))

        val payloads = AccountDecider.from(events).decide(command)
        if (payloads.isEmpty()) return Outcome.success()

        when (val outcome = append(accountId, expectedVersion = version, events = payloads.stamp(accountId, clock))) {
            is Outcome.Success -> return Outcome.success()
            is Outcome.Failure -> when (val failure = outcome.error) {
                AccountAppendFailure.VersionConflict -> Unit
                is AccountAppendFailure.EmailAddressAlreadyUsed ->
                    return Outcome.Failure(AccountError.EmailAddressAlreadyUsed(failure.emailAddress))
            }
        }
    }
    return Outcome.Failure(ConcurrentModification(accountId))
}
