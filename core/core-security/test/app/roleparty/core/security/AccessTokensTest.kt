package app.roleparty.core.security

import app.roleparty.account.commons.AccountId
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.time.toJavaInstant

private fun newKeyPair(): KeyPair =
    KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()

private fun KeyPair.encodedPublic() = Base64.encode(public.encoded)

private fun KeyPair.token(accountId: AccountId, now: Instant = Clock.System.now()): String =
    JWT.create()
        .withKeyId(AccessTokenKeys.idOf(public as ECPublicKey))
        .withIssuer(AccessTokenKeys.ISSUER)
        .withSubject(accountId.toString())
        .withIssuedAt(now.toJavaInstant())
        .withExpiresAt((now + 15.minutes).toJavaInstant())
        .sign(Algorithm.ECDSA256(public as ECPublicKey, private as ECPrivateKey))

private fun ApplicationTestBuilder.protectedRoute(vararg env: Pair<String, String>) {
    application {
        installAuthentication(mapOf(*env))
        routing {
            authenticated { get("/me") { call.respondText(call.user().accountId.toString()) } }
        }
    }
}

class AccessTokensTest : FunSpec({
    val keys = newKeyPair()
    val accountId = AccountId.generate()

    test("a token should authenticate its account, with only the public key on the verifying side") {
        testApplication {
            protectedRoute("JWT_PUBLIC_KEY" to keys.encodedPublic())

            val response = client.get("/me") { bearerAuth(keys.token(accountId)) }

            response.status shouldBe HttpStatusCode.OK
            response.bodyAsText() shouldBe accountId.toString()
        }
    }

    test("a token signed with the previous key should still work during a rotation") {
        testApplication {
            val newKeys = newKeyPair()
            protectedRoute("JWT_PUBLIC_KEY" to newKeys.encodedPublic(), "JWT_PREVIOUS_PUBLIC_KEY" to keys.encodedPublic())

            client.get("/me") { bearerAuth(keys.token(accountId)) }.status shouldBe HttpStatusCode.OK
            client.get("/me") { bearerAuth(newKeys.token(accountId)) }.status shouldBe HttpStatusCode.OK
        }
    }

    test("a token signed with an unknown key should be refused") {
        testApplication {
            protectedRoute("JWT_PUBLIC_KEY" to keys.encodedPublic())

            client.get("/me") { bearerAuth(newKeyPair().token(accountId)) }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    test("a token forged with the public key as an HMAC secret should be refused") {
        testApplication {
            protectedRoute("JWT_PUBLIC_KEY" to keys.encodedPublic())
            val legitimate = JWT.decode(keys.token(accountId))
            val forged = JWT.create()
                .withKeyId(legitimate.keyId)
                .withIssuer(legitimate.issuer)
                .withSubject(accountId.toString())
                .sign(Algorithm.HMAC256(keys.public.encoded))

            client.get("/me") { bearerAuth(forged) }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    test("an expired token should be refused") {
        testApplication {
            protectedRoute("JWT_PUBLIC_KEY" to keys.encodedPublic())
            client.get("/me") { bearerAuth(keys.token(accountId, Clock.System.now() - 1.hours)) }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    test("garbage and missing tokens should be refused") {
        testApplication {
            protectedRoute("JWT_PUBLIC_KEY" to keys.encodedPublic())

            client.get("/me") { bearerAuth("garbage") }.status shouldBe HttpStatusCode.Unauthorized
            client.get("/me").status shouldBe HttpStatusCode.Unauthorized
        }
    }
})
