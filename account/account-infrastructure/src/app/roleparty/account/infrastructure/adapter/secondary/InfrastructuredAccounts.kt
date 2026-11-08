package app.roleparty.account.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.database.AccountTable
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError.EmailAddressAlreadyUsed
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.account.event.AccountEmailAddressChanged
import app.roleparty.account.event.AccountRegistered
import app.roleparty.account.event.AccountRoleChanged
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.outbox.enqueue
import io.r2dbc.spi.R2dbcException
import kotlin.time.Clock
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

internal class InfrastructuredAccounts(
    private val database: R2dbcDatabase,
    private val clock: Clock,
) : Accounts {

    override suspend fun register(account: Account) = rejectingUsed(account.emailAddress) {
        AccountTable.insert {
            it[id] = account.id
            it[emailAddress] = account.emailAddress
            it[role] = account.role
        }
        enqueue(AccountRegistered(account.id, account.emailAddress, account.role), clock.now())
    }

    override suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress) = rejectingUsed(emailAddress) {
        val updated = AccountTable.update({ AccountTable.id eq accountId }) { it[AccountTable.emailAddress] = emailAddress }
        if (updated > 0) enqueue(AccountEmailAddressChanged(accountId, emailAddress), clock.now())
    }

    override suspend fun changeRole(accountId: AccountId, role: AccountRole) {
        suspendTransaction(database) {
            val updated = AccountTable.update({ AccountTable.id eq accountId }) { it[AccountTable.role] = role }
            if (updated > 0) enqueue(AccountRoleChanged(accountId, role), clock.now())
        }
    }

    override suspend fun findById(accountId: AccountId) = findWhere { AccountTable.id eq accountId }

    override suspend fun findByEmailAddress(emailAddress: EmailAddress) = findWhere { AccountTable.emailAddress eq emailAddress }

    private suspend fun findWhere(condition: () -> Op<Boolean>): Account? =
        suspendTransaction(database) {
            AccountTable.selectAll().where(condition()).firstOrNull()?.let {
                Account(it[AccountTable.id], it[AccountTable.emailAddress], it[AccountTable.role])
            }
        }

    private suspend fun rejectingUsed(
        emailAddress: EmailAddress,
        write: suspend R2dbcTransaction.() -> Unit,
    ): Outcome<Unit, EmailAddressAlreadyUsed> =
        try {
            suspendTransaction(database) {
                try {
                    write()
                } catch (exception: R2dbcException) {
                    if (exception.isUniqueViolation()) throw EmailAddressTaken()
                    throw exception
                }
            }
            Outcome.success()
        } catch (_: EmailAddressTaken) {
            Outcome.Failure(EmailAddressAlreadyUsed(emailAddress))
        }

    private fun Throwable.isUniqueViolation() =
        generateSequence(this) { it.cause }.any { (it as? R2dbcException)?.sqlState == UNIQUE_VIOLATION }

    private class EmailAddressTaken : Exception()

    private companion object {
        const val UNIQUE_VIOLATION = "23505"
    }
}
