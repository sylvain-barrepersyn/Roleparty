package app.roleparty.security.domain.model

import kotlin.time.Instant

data class IssuedToken<T>(val value: T, val expiration: Instant)

data class Authentication(val accessToken: IssuedToken<AccessToken>, val refreshToken: IssuedToken<RefreshToken>)
