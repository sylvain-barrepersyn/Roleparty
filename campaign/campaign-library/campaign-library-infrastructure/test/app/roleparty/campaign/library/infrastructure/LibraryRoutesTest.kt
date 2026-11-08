package app.roleparty.campaign.library.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.library.contract.web.LibraryEntryCreatedResponseBody
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.infrastructure.adapter.primary.LibraryLocalization
import app.roleparty.campaign.library.test.LibraryFixture
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import app.roleparty.media.commons.ImageId
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
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

class LibraryRoutesTest : FunSpec({

    val keys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    val publicKey = keys.public as ECPublicKey

    fun tokenOf(accountId: AccountId): String {
        val now = Clock.System.now()
        return JWT.create()
            .withKeyId(AccessTokenKeys.idOf(publicKey))
            .withIssuer(AccessTokenKeys.ISSUER)
            .withSubject(accountId.toString())
            .withIssuedAt(now.toJavaInstant())
            .withExpiresAt((now + 15.minutes).toJavaInstant())
            .sign(Algorithm.ECDSA256(publicKey, keys.private as ECPrivateKey))
    }

    val localization = object : LibraryLocalization {
        override suspend fun respond(call: ApplicationCall, error: LibraryError) = when (error) {
            LibraryError.GameMasterRequired -> call.respond(HttpStatusCode.Forbidden)
            is LibraryError.LibraryEntryNotFound, is LibraryError.CampaignNotFound -> call.respond(HttpStatusCode.NotFound)
            LibraryError.BlankTitle -> call.respond(HttpStatusCode.BadRequest)
        }
    }

    test("the game master should add an entry and rename it, but nobody else") {
        testApplication {
            val fixtures = LibraryFixture()
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val imageId = ImageId.generate()
            application {
                install(ContentNegotiation) { json() }
                installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
                serveLibraryRoutes(fixtures.module, localization)
            }

            val created = client.post("/v1/campaigns/$campaignId/library") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody("""{"title":"Cave","description":"Dark.","imageId":"$imageId"}""")
            }
            created.status shouldBe HttpStatusCode.Created
            val id = Json.decodeFromString<LibraryEntryCreatedResponseBody>(created.bodyAsText()).id

            client.put("/v1/campaigns/$campaignId/library/$id/metadata") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody("""{"title":"Deep cave","description":"Darker."}""")
            }.status shouldBe HttpStatusCode.NoContent

            client.put("/v1/campaigns/$campaignId/library/$id/metadata") {
                bearerAuth(tokenOf(AccountId.generate()))
                contentType(ContentType.Application.Json)
                setBody("""{"title":"Mine","description":""}""")
            }.status shouldBe HttpStatusCode.Forbidden

            val listed = client.get("/v1/campaigns/$campaignId/library") { bearerAuth(tokenOf(gameMaster)) }
            Json.decodeFromString<List<LibraryEntryResponseBody>>(listed.bodyAsText()) shouldBe
                listOf(LibraryEntryResponseBody(id, "Deep cave", "Darker.", imageId))
        }
    }
})
