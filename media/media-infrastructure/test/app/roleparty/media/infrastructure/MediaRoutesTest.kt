package app.roleparty.media.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import app.roleparty.media.contract.web.ImageUploadedResponseBody
import app.roleparty.media.domain.model.Image
import app.roleparty.media.domain.model.MediaError
import app.roleparty.media.infrastructure.adapter.primary.MediaLocalization
import app.roleparty.media.test.MediaFixture
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaInstant
import kotlinx.serialization.json.Json

class MediaRoutesTest : FunSpec({

    val keys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    val publicKey = keys.public as ECPublicKey

    fun accessToken(): String {
        val now = Clock.System.now()
        return JWT.create()
            .withKeyId(AccessTokenKeys.idOf(publicKey))
            .withIssuer(AccessTokenKeys.ISSUER)
            .withSubject(AccountId.generate().toString())
            .withIssuedAt(now.toJavaInstant())
            .withExpiresAt((now + 15.minutes).toJavaInstant())
            .sign(Algorithm.ECDSA256(publicKey, keys.private as ECPrivateKey))
    }

    val localization = object : MediaLocalization {
        override suspend fun respond(call: ApplicationCall, error: MediaError) = when (error) {
            is MediaError.ImageTooLarge -> call.respond(HttpStatusCode.PayloadTooLarge)
            is MediaError.InvalidImage -> call.respond(HttpStatusCode.BadRequest)
        }
    }

    fun ApplicationTestBuilder.media(): MediaFixture {
        val fixtures = MediaFixture()
        application {
            install(ContentNegotiation) { json() }
            installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
            serveMediaRoutes(fixtures.module, localization)
        }
        return fixtures
    }

    test("an uploaded image should be served back with its media type") {
        testApplication {
            media()

            val upload = client.post("/v1/images") {
                bearerAuth(accessToken())
                contentType(ContentType.Image.JPEG)
                setBody(MediaFixture.JPEG_CONTENT)
            }
            upload.status shouldBe HttpStatusCode.Created
            val imageId = Json.decodeFromString<ImageUploadedResponseBody>(upload.bodyAsText()).id

            val image = client.get("/v1/images/$imageId")
            image.status shouldBe HttpStatusCode.OK
            image.headers[HttpHeaders.ContentType] shouldBe "image/jpeg"
            image.headers["X-Content-Type-Options"] shouldBe "nosniff"
            image.bodyAsBytes().contentEquals(MediaFixture.JPEG_CONTENT) shouldBe true
        }
    }

    test("an upload should require an access token") {
        testApplication {
            val fixtures = media()

            val upload = client.post("/v1/images") {
                contentType(ContentType.Image.JPEG)
                setBody(MediaFixture.JPEG_CONTENT)
            }

            upload.status shouldBe HttpStatusCode.Unauthorized
            fixtures.storedImages() shouldBe 0
        }
    }

    test("an unsupported media type should be refused") {
        testApplication {
            media()

            val upload = client.post("/v1/images") {
                bearerAuth(accessToken())
                contentType(ContentType.Image.PNG)
                setBody(MediaFixture.JPEG_CONTENT)
            }

            upload.status shouldBe HttpStatusCode.UnsupportedMediaType
        }
    }

    test("content that is not a JPEG image should be refused") {
        testApplication {
            media()

            val upload = client.post("/v1/images") {
                bearerAuth(accessToken())
                contentType(ContentType.Image.JPEG)
                setBody("<html></html>".toByteArray())
            }

            upload.status shouldBe HttpStatusCode.BadRequest
        }
    }

    test("an image larger than the limit should be refused") {
        testApplication {
            val fixtures = media()

            val upload = client.post("/v1/images") {
                bearerAuth(accessToken())
                contentType(ContentType.Image.JPEG)
                setBody(MediaFixture.JPEG_CONTENT + ByteArray(Image.MAX_BYTES))
            }

            upload.status shouldBe HttpStatusCode.PayloadTooLarge
            fixtures.storedImages() shouldBe 0
        }
    }

    test("an unknown image identifier should not be found, and a malformed one refused") {
        testApplication {
            media()

            client.get("/v1/images/0196f1c2-7c3e-7a4b-9e1d-2f3a4b5c6d7e").status shouldBe HttpStatusCode.NotFound
            client.get("/v1/images/not-an-id").status shouldBe HttpStatusCode.BadRequest
        }
    }
})
