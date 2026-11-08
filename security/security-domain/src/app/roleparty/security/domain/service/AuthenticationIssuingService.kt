package app.roleparty.security.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.security.domain.model.Authentication
import app.roleparty.security.domain.model.DeviceId
import app.roleparty.security.domain.model.IssuedToken
import app.roleparty.security.domain.model.RefreshToken
import app.roleparty.security.domain.model.StoredRefreshToken
import app.roleparty.security.domain.port.secondary.RefreshTokens
import kotlin.time.Clock

internal class AuthenticationIssuingService(
    private val refreshTokens: RefreshTokens,
    private val accessTokenIssuingService: AccessTokenIssuingService,
    private val clock: Clock,
) {

    suspend fun issue(accountId: AccountId, deviceId: DeviceId): Authentication {
        val refreshToken = RefreshToken.generate()
        val expiration = clock.now() + REFRESH_TOKEN_DURATION
        refreshTokens.save(StoredRefreshToken(refreshToken.hash(), accountId, deviceId, expiration, rotatedAt = null, revoked = false))
        return Authentication(accessTokenIssuingService.issue(accountId), IssuedToken(refreshToken, expiration))
    }
}
