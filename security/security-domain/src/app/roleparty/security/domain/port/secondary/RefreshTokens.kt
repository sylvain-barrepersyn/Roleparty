package app.roleparty.security.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.security.domain.model.DeviceId
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.model.StoredRefreshToken
import kotlin.time.Instant

interface RefreshTokens {

    suspend fun save(token: StoredRefreshToken)

    suspend fun find(hash: SecretHash): StoredRefreshToken?

    suspend fun markRotated(hash: SecretHash, at: Instant): Boolean

    suspend fun revokeDevice(accountId: AccountId, deviceId: DeviceId)

    suspend fun revokeAllFor(accountId: AccountId)

    suspend fun deleteExpired(before: Instant)
}
