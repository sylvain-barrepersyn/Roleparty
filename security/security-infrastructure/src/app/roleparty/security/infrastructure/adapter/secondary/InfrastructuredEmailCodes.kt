package app.roleparty.security.infrastructure.adapter.secondary

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.database.EmailCodeTable
import app.roleparty.security.domain.model.EmailCode
import app.roleparty.security.domain.model.EmailCodePurpose
import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.port.secondary.EmailCodes
import kotlin.time.Instant
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredEmailCodes(private val database: R2dbcDatabase) : EmailCodes {

    override suspend fun save(code: EmailCode) {
        suspendTransaction(database) {
            EmailCodeTable.insert {
                it[codeHash] = code.hash.toString()
                it[purpose] = code.purpose.name
                it[emailAddress] = code.emailAddress
                it[passwordHash] = code.passwordHash?.toString()
                it[expiration] = code.expiration
            }
        }
    }

    override suspend fun find(hash: SecretHash, purpose: EmailCodePurpose, emailAddress: EmailAddress): EmailCode? =
        suspendTransaction(database) {
            EmailCodeTable.selectAll()
                .where {
                    (EmailCodeTable.codeHash eq hash.toString()) and (EmailCodeTable.purpose eq purpose.name) and (EmailCodeTable.emailAddress eq emailAddress)
                }
                .firstOrNull()
                ?.let {
                    EmailCode(hash, purpose, emailAddress, it[EmailCodeTable.passwordHash]?.let(PasswordHash::from), it[EmailCodeTable.expiration])
                }
        }

    override suspend fun deleteAll(purpose: EmailCodePurpose, emailAddress: EmailAddress) {
        suspendTransaction(database) {
            EmailCodeTable.deleteWhere { (EmailCodeTable.purpose eq purpose.name) and (EmailCodeTable.emailAddress eq emailAddress) }
        }
    }

    override suspend fun deleteExpired(before: Instant) {
        suspendTransaction(database) {
            EmailCodeTable.deleteWhere { expiration lessEq before }
        }
    }
}
