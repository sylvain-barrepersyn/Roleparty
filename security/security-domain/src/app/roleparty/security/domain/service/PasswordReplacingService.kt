package app.roleparty.security.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.security.domain.port.secondary.Credentials
import app.roleparty.security.domain.port.secondary.RefreshTokens

internal class PasswordReplacingService(
    private val credentials: Credentials,
    private val passwordHashingService: PasswordHashingService,
    private val refreshTokens: RefreshTokens,
) {

    suspend fun replace(accountId: AccountId, newPassword: String) {
        credentials.changePasswordHash(accountId, passwordHashingService.hash(newPassword))
        refreshTokens.revokeAllFor(accountId)
    }
}
