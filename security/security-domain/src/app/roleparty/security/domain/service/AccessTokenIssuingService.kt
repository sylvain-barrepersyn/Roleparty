package app.roleparty.security.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.security.domain.model.AccessToken
import app.roleparty.security.domain.model.IssuedToken
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import kotlin.time.Clock
import kotlin.time.toJavaInstant

internal class AccessTokenIssuingService(
    publicKey: ECPublicKey,
    privateKey: ECPrivateKey,
    private val clock: Clock,
) {

    private val algorithm = Algorithm.ECDSA256(publicKey, privateKey)
    private val keyId = AccessTokenKeys.idOf(publicKey)

    fun issue(accountId: AccountId): IssuedToken<AccessToken> {
        val now = clock.now()
        val expiration = now + ACCESS_TOKEN_DURATION
        val token = JWT.create()
            .withKeyId(keyId)
            .withIssuer(AccessTokenKeys.ISSUER)
            .withSubject(accountId.toString())
            .withIssuedAt(now.toJavaInstant())
            .withExpiresAt(expiration.toJavaInstant())
            .sign(algorithm)
        return IssuedToken(AccessToken.from(token), expiration)
    }
}
