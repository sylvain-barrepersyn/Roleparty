package app.roleparty.security.contract.web

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequestBody(val refreshToken: String)
