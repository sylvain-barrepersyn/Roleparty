package app.roleparty.security.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.security.domain.model.RefreshToken

interface SignOut {

    suspend fun fromDevice(refreshToken: RefreshToken)

    suspend fun everywhere(accountId: AccountId)
}
