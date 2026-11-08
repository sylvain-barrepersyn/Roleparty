package app.roleparty.security.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.security.database.RefreshTokenTable
import app.roleparty.security.domain.model.DeviceId
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.model.StoredRefreshToken
import app.roleparty.security.domain.port.secondary.RefreshTokens
import kotlin.time.Instant
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

internal class InfrastructuredRefreshTokens(private val database: R2dbcDatabase) : RefreshTokens {

    override suspend fun save(token: StoredRefreshToken) {
        suspendTransaction(database) {
            RefreshTokenTable.insert {
                it[tokenHash] = token.hash.toString()
                it[accountId] = token.accountId
                it[deviceId] = token.deviceId.toUuid()
                it[expiration] = token.expiration
                it[rotatedAt] = token.rotatedAt
                it[revoked] = token.revoked
            }
        }
    }

    override suspend fun find(hash: SecretHash): StoredRefreshToken? =
        suspendTransaction(database) {
            RefreshTokenTable.selectAll().where { RefreshTokenTable.tokenHash eq hash.toString() }.firstOrNull()?.let {
                StoredRefreshToken(
                    hash = hash,
                    accountId = it[RefreshTokenTable.accountId],
                    deviceId = DeviceId.from(it[RefreshTokenTable.deviceId]),
                    expiration = it[RefreshTokenTable.expiration],
                    rotatedAt = it[RefreshTokenTable.rotatedAt],
                    revoked = it[RefreshTokenTable.revoked],
                )
            }
        }

    override suspend fun markRotated(hash: SecretHash, at: Instant): Boolean =
        suspendTransaction(database) {
            RefreshTokenTable.update({ (RefreshTokenTable.tokenHash eq hash.toString()) and RefreshTokenTable.rotatedAt.isNull() }) {
                it[rotatedAt] = at
            } > 0
        }

    override suspend fun revokeDevice(accountId: AccountId, deviceId: DeviceId) {
        suspendTransaction(database) {
            RefreshTokenTable.update({ (RefreshTokenTable.accountId eq accountId) and (RefreshTokenTable.deviceId eq deviceId.toUuid()) }) {
                it[revoked] = true
            }
        }
    }

    override suspend fun revokeAllFor(accountId: AccountId) {
        suspendTransaction(database) {
            RefreshTokenTable.update({ RefreshTokenTable.accountId eq accountId }) { it[revoked] = true }
        }
    }

    override suspend fun deleteExpired(before: Instant) {
        suspendTransaction(database) {
            RefreshTokenTable.deleteWhere { expiration lessEq before }
        }
    }
}
