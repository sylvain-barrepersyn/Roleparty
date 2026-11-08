package app.roleparty.security.contract.web

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationResponseBody(val accessToken: Token, val refreshToken: Token) {

    @Serializable
    data class Token(val value: String, val expirationDate: Instant)
}
