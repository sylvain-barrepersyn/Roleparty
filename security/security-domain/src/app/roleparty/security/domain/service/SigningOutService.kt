package app.roleparty.security.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.security.domain.model.RefreshToken
import app.roleparty.security.domain.port.primary.SignOut
import app.roleparty.security.domain.port.secondary.RefreshTokens

internal class SigningOutService(private val refreshTokens: RefreshTokens) : SignOut {

    override suspend fun fromDevice(refreshToken: RefreshToken) {
        val token = refreshTokens.find(refreshToken.hash()) ?: return
        refreshTokens.revokeDevice(token.accountId, token.deviceId)
    }

    override suspend fun everywhere(accountId: AccountId) = refreshTokens.revokeAllFor(accountId)
}
