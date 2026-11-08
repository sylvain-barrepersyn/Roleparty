package app.roleparty.security.test

import app.roleparty.account.commons.AccountId
import app.roleparty.security.domain.model.DeviceId
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.model.StoredRefreshToken
import app.roleparty.security.domain.port.secondary.RefreshTokens
import kotlin.time.Instant

internal class InMemoryRefreshTokens : RefreshTokens {

    private val tokens = mutableMapOf<SecretHash, StoredRefreshToken>()

    override suspend fun save(token: StoredRefreshToken) {
        tokens[token.hash] = token
    }

    override suspend fun find(hash: SecretHash) = tokens[hash]

    override suspend fun markRotated(hash: SecretHash, at: Instant): Boolean {
        val token = tokens[hash]?.takeIf { it.rotatedAt == null } ?: return false
        tokens[hash] = token.copy(rotatedAt = at)
        return true
    }

    override suspend fun revokeDevice(accountId: AccountId, deviceId: DeviceId) =
        tokens.replaceAll { _, it -> if (it.accountId == accountId && it.deviceId == deviceId) it.copy(revoked = true) else it }

    override suspend fun revokeAllFor(accountId: AccountId) =
        tokens.replaceAll { _, it -> if (it.accountId == accountId) it.copy(revoked = true) else it }

    override suspend fun deleteExpired(before: Instant) {
        tokens.values.removeIf { it.expiration <= before }
    }

    fun all(): List<StoredRefreshToken> = tokens.values.toList()
}
