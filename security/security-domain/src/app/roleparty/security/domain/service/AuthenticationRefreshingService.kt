package app.roleparty.security.domain.service

import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.Authentication
import app.roleparty.security.domain.model.RefreshToken
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.domain.port.primary.RefreshAuthentication
import app.roleparty.security.domain.port.secondary.RefreshTokens
import kotlin.time.Clock

internal class AuthenticationRefreshingService(
    private val refreshTokens: RefreshTokens,
    private val authenticationIssuingService: AuthenticationIssuingService,
    private val clock: Clock,
) : RefreshAuthentication {

    override suspend fun invoke(refreshToken: RefreshToken): Outcome<Authentication, SecurityError> {
        val hash = refreshToken.hash()
        val token = refreshTokens.find(hash)
        val now = clock.now()

        if (token == null || token.revoked || token.expiration <= now) return Outcome.Failure(SecurityError.InvalidRefreshToken)

        val rotatedAt = token.rotatedAt
        if (rotatedAt != null && now - rotatedAt > REFRESH_RETRY_GRACE) {
            refreshTokens.revokeDevice(token.accountId, token.deviceId)
            return Outcome.Failure(SecurityError.InvalidRefreshToken)
        }

        refreshTokens.markRotated(hash, now)
        return Outcome.Success(authenticationIssuingService.issue(token.accountId, token.deviceId))
    }
}
