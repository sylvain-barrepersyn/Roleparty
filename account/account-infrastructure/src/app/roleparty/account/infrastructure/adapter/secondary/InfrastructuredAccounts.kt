package app.roleparty.account.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.database.AccountEventTable
import app.roleparty.account.database.AccountOverviewTable
import app.roleparty.account.database.StoredAccountEventPayload
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.model.AccountHistory
import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.account.domain.model.AccountVersion
import app.roleparty.account.domain.port.secondary.AccountAppendFailure
import app.roleparty.account.domain.port.secondary.AccountOverviews
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.account.event.AccountEmailAddressChanged
import app.roleparty.account.event.AccountRegistered
import app.roleparty.account.event.AccountRoleChanged
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.outbox.enqueue
import io.r2dbc.spi.R2dbcException
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

class InfrastructuredAccounts internal constructor(private val database: R2dbcDatabase) : Accounts, AccountOverviews {

    override suspend fun append(
        accountId: AccountId,
        expectedVersion: AccountVersion,
        events: List<AccountEvent>,
    ): Outcome<Unit, AccountAppendFailure> {
        require(events.isNotEmpty()) { "Cannot append an empty list of events to account $accountId" }
        require(events.all { it.accountId == accountId }) { "Cannot append events of another account to account $accountId" }

        return try {
            suspendTransaction(database) {
                val stream = eventsOf(accountId)
                if (stream.size != expectedVersion.value) throw AppendRejected(AccountAppendFailure.VersionConflict)

                rejectingDuplicates(AccountAppendFailure.VersionConflict) {
                    events.forEachIndexed { index, event -> insert(event, version = stream.size + index + 1) }
                }

                val overview = AccountOverview.from(stream + events)
                rejectingDuplicates(AccountAppendFailure.EmailAddressAlreadyUsed(overview.emailAddress)) {
                    save(overview)
                }

                events.forEach { enqueuePublicEvent(it) }
            }
            Outcome.success()
        } catch (rejected: AppendRejected) {
            Outcome.Failure(rejected.failure)
        }
    }

    override suspend fun load(accountId: AccountId): AccountHistory {
        val events = suspendTransaction(database) { eventsOf(accountId) }
        return AccountHistory(events, AccountVersion.from(events.size))
    }

    override suspend fun findById(accountId: AccountId) =
        findOverviewWhere { AccountOverviewTable.accountId eq accountId }

    override suspend fun findByEmailAddress(emailAddress: EmailAddress) =
        findOverviewWhere { AccountOverviewTable.emailAddress eq emailAddress }

    private suspend fun eventsOf(accountId: AccountId): List<AccountEvent> =
        AccountEventTable.selectAll()
            .where { AccountEventTable.accountId eq accountId }
            .orderBy(AccountEventTable.version, SortOrder.ASC)
            .map {
                AccountEvent(
                    id = AccountEvent.Id.from(it[AccountEventTable.id].toString()),
                    accountId = accountId,
                    occurredAt = it[AccountEventTable.occurredAt],
                    payload = it[AccountEventTable.payload].toDomain(),
                )
            }
            .toList()

    private suspend fun insert(event: AccountEvent, version: Int) {
        AccountEventTable.insert {
            it[accountId] = event.accountId
            it[AccountEventTable.version] = version
            it[id] = event.id.toUuid()
            it[occurredAt] = event.occurredAt
            it[payload] = event.payload.toStored()
        }
    }

    private suspend fun save(overview: AccountOverview) {
        AccountOverviewTable.upsert {
            it[accountId] = overview.accountId
            it[emailAddress] = overview.emailAddress
            it[role] = overview.role
        }
    }

    private suspend fun findOverviewWhere(condition: () -> Op<Boolean>): AccountOverview? =
        suspendTransaction(database) {
            AccountOverviewTable.selectAll().where(condition()).firstOrNull()?.let {
                AccountOverview.from(
                    accountId = it[AccountOverviewTable.accountId],
                    emailAddress = it[AccountOverviewTable.emailAddress],
                    role = it[AccountOverviewTable.role],
                )
            }
        }

    private suspend fun R2dbcTransaction.enqueuePublicEvent(event: AccountEvent) {
        val id = event.id.toUuid()
        when (val payload = event.payload) {
            is Payload.Registered ->
                enqueue(AccountRegistered(event.accountId, payload.emailAddress, payload.role), event.occurredAt, id)
            is Payload.EmailAddressChanged ->
                enqueue(AccountEmailAddressChanged(event.accountId, payload.emailAddress), event.occurredAt, id)
            is Payload.RoleChanged ->
                enqueue(AccountRoleChanged(event.accountId, payload.role), event.occurredAt, id)
        }
    }

    private suspend fun rejectingDuplicates(failure: AccountAppendFailure, block: suspend () -> Unit) {
        try {
            block()
        } catch (exception: R2dbcException) {
            if (exception.isUniqueViolation()) throw AppendRejected(failure)
            throw exception
        }
    }

    private fun Throwable.isUniqueViolation() =
        generateSequence(this) { it.cause }.any { (it as? R2dbcException)?.sqlState == UNIQUE_VIOLATION }

    private fun AccountEvent.Id.toUuid() = Uuid.parse(toString())

    private fun Payload.toStored(): StoredAccountEventPayload = when (this) {
        is Payload.Registered -> StoredAccountEventPayload.Registered(emailAddress, role)
        is Payload.EmailAddressChanged -> StoredAccountEventPayload.EmailAddressChanged(emailAddress)
        is Payload.RoleChanged -> StoredAccountEventPayload.RoleChanged(role)
    }

    private fun StoredAccountEventPayload.toDomain(): Payload = when (this) {
        is StoredAccountEventPayload.Registered -> Payload.Registered(emailAddress, role)
        is StoredAccountEventPayload.EmailAddressChanged -> Payload.EmailAddressChanged(emailAddress)
        is StoredAccountEventPayload.RoleChanged -> Payload.RoleChanged(role)
    }

    private class AppendRejected(val failure: AccountAppendFailure) : Exception()

    private companion object {
        const val UNIQUE_VIOLATION = "23505"
    }
}
