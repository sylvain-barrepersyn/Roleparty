package app.roleparty.security.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.test.ControlledClock
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class AccessTokenIssuingServiceTest : FunSpec({
    val keys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    val publicKey = keys.public as ECPublicKey
    val now = Instant.parse("2026-11-07T12:00:00Z")
    val issuer = AccessTokenIssuingService(publicKey, keys.private as ECPrivateKey, ControlledClock(now))

    test("a token should be signed for its account, and expire after 15 minutes") {
        val accountId = AccountId.generate()

        val issued = issuer.issue(accountId)

        val token = JWT.decode(issued.value.toString()).also { Algorithm.ECDSA256(publicKey, null).verify(it) }
        token.subject shouldBe accountId.toString()
        token.issuer shouldBe AccessTokenKeys.ISSUER
        token.keyId shouldBe AccessTokenKeys.idOf(publicKey)
        token.expiresAtAsInstant shouldBe (now + 15.minutes).toJavaInstant()
        issued.expiration shouldBe now + 15.minutes
    }
})
