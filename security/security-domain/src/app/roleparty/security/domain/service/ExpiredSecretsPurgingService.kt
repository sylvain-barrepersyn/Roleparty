package app.roleparty.security.domain.service

import app.roleparty.security.domain.port.primary.PurgeExpiredSecrets
import app.roleparty.security.domain.port.secondary.EmailCodes
import app.roleparty.security.domain.port.secondary.RefreshTokens
import kotlin.time.Clock

internal class ExpiredSecretsPurgingService(
    private val refreshTokens: RefreshTokens,
    private val emailCodes: EmailCodes,
    private val clock: Clock,
) : PurgeExpiredSecrets {

    override suspend fun invoke() {
        refreshTokens.deleteExpired(clock.now())
        emailCodes.deleteExpired(clock.now())
    }
}
