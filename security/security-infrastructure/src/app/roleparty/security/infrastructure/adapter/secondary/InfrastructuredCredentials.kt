package app.roleparty.security.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.database.CredentialTable
import app.roleparty.security.domain.model.Credential
import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.port.secondary.Credentials
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insertIgnore
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

internal class InfrastructuredCredentials(private val database: R2dbcDatabase) : Credentials {

    override suspend fun saveIfAbsent(credential: Credential) {
        suspendTransaction(database) {
            CredentialTable.insertIgnore {
                it[accountId] = credential.accountId
                it[emailAddress] = credential.emailAddress
                it[passwordHash] = credential.passwordHash.toString()
            }
        }
    }

    override suspend fun findByEmailAddress(emailAddress: EmailAddress) = findWhere { CredentialTable.emailAddress eq emailAddress }

    override suspend fun findByAccountId(accountId: AccountId) = findWhere { CredentialTable.accountId eq accountId }

    override suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress) {
        suspendTransaction(database) {
            CredentialTable.update({ CredentialTable.accountId eq accountId }) { it[CredentialTable.emailAddress] = emailAddress }
        }
    }

    override suspend fun changePasswordHash(accountId: AccountId, passwordHash: PasswordHash) {
        suspendTransaction(database) {
            CredentialTable.update({ CredentialTable.accountId eq accountId }) { it[CredentialTable.passwordHash] = passwordHash.toString() }
        }
    }

    private suspend fun findWhere(condition: () -> Op<Boolean>): Credential? =
        suspendTransaction(database) {
            CredentialTable.selectAll().where(condition()).firstOrNull()?.let {
                Credential(it[CredentialTable.accountId], it[CredentialTable.emailAddress], PasswordHash.from(it[CredentialTable.passwordHash]))
            }
        }
}
