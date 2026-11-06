package app.roleparty.account.test

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.model.AccountHistory
import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.account.domain.model.AccountVersion
import app.roleparty.account.domain.port.secondary.AccountAppendFailure
import app.roleparty.account.domain.port.secondary.AccountOverviews
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

class InMemoryAccounts : Accounts, AccountOverviews {

    private val streams = mutableMapOf<AccountId, List<AccountEvent>>()

    override suspend fun append(
        accountId: AccountId,
        expectedVersion: AccountVersion,
        events: List<AccountEvent>,
    ): Outcome<Unit, AccountAppendFailure> {
        require(events.isNotEmpty()) { "Cannot append an empty list of events to account $accountId" }
        require(events.all { it.accountId == accountId }) { "Cannot append events of another account to account $accountId" }

        val stream = streams[accountId].orEmpty()
        if (AccountVersion.from(stream.size) != expectedVersion) return Outcome.Failure(AccountAppendFailure.VersionConflict)

        val assignedEmailAddress = events.currentEmailAddress()
        if (assignedEmailAddress != null && isHeldByAnotherAccount(assignedEmailAddress, accountId)) {
            return Outcome.Failure(AccountAppendFailure.EmailAddressAlreadyUsed(assignedEmailAddress))
        }

        streams[accountId] = stream + events
        return Outcome.success()
    }

    private fun isHeldByAnotherAccount(emailAddress: EmailAddress, accountId: AccountId) =
        streams.any { it.key != accountId && it.value.currentEmailAddress() == emailAddress }

    private fun List<AccountEvent>.currentEmailAddress() = mapNotNull { event ->
        when (val payload = event.payload) {
            is Payload.Registered -> payload.emailAddress
            is Payload.EmailAddressChanged -> payload.emailAddress
            is Payload.RoleChanged -> null
        }
    }.lastOrNull()

    override suspend fun load(accountId: AccountId): AccountHistory {
        val events = streams[accountId].orEmpty()
        return AccountHistory(events, AccountVersion.from(events.size))
    }

    override suspend fun findById(accountId: AccountId) =
        streams[accountId]?.let(AccountOverview::from)

    override suspend fun findByEmailAddress(emailAddress: EmailAddress) =
        streams.values.map(AccountOverview::from).find { it.emailAddress == emailAddress }
}
