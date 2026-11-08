package app.roleparty.campaign.story.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.story.contract.web.SceneCreatedResponseBody
import app.roleparty.campaign.story.contract.web.SceneResponseBody
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.infrastructure.adapter.primary.StoryLocalization
import app.roleparty.campaign.story.test.StoryFixture
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
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

class StoryRoutesTest : FunSpec({

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

    val localization = object : StoryLocalization {
        override suspend fun respond(call: ApplicationCall, error: StoryError) = when (error) {
            StoryError.GameMasterRequired -> call.respond(HttpStatusCode.Forbidden)
            is StoryError.SceneNotFound, is StoryError.LibraryEntryNotFound, is StoryError.CampaignNotFound -> call.respond(HttpStatusCode.NotFound)
            StoryError.BlankName -> call.respond(HttpStatusCode.BadRequest)
        }
    }

    test("the game master should create, change, link, list and delete scenes, but nobody else") {
        testApplication {
            val fixtures = StoryFixture()
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val entryId = fixtures.`given a library entry`(campaignId)
            application {
                install(ContentNegotiation) { json() }
                installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
                serveStoryRoutes(fixtures.module, localization)
            }

            client.post("/v1/campaigns/$campaignId/scenes") {
                bearerAuth(tokenOf(AccountId.generate()))
                contentType(ContentType.Application.Json)
                setBody("""{"name":"Ambush","description":""}""")
            }.status shouldBe HttpStatusCode.Forbidden

            val created = client.post("/v1/campaigns/$campaignId/scenes") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody("""{"name":"Ambush","description":""}""")
            }
            created.status shouldBe HttpStatusCode.Created
            val id = Json.decodeFromString<SceneCreatedResponseBody>(created.bodyAsText()).id

            client.put("/v1/campaigns/$campaignId/scenes/$id") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody("""{"name":"Night ambush","description":"In the dark."}""")
            }.status shouldBe HttpStatusCode.NoContent

            client.put("/v1/campaigns/$campaignId/scenes/$id/library/$entryId") { bearerAuth(tokenOf(gameMaster)) }.status shouldBe HttpStatusCode.NoContent

            val listed = client.get("/v1/campaigns/$campaignId/scenes") { bearerAuth(tokenOf(gameMaster)) }
            Json.decodeFromString<List<SceneResponseBody>>(listed.bodyAsText()) shouldBe listOf(SceneResponseBody(id, "Night ambush", "In the dark.", listOf(entryId)))

            client.delete("/v1/campaigns/$campaignId/scenes/$id/library/$entryId") { bearerAuth(tokenOf(gameMaster)) }.status shouldBe HttpStatusCode.NoContent
            fixtures.scene(id)?.libraryEntries shouldBe emptyList()

            client.delete("/v1/campaigns/$campaignId/scenes/$id") { bearerAuth(tokenOf(gameMaster)) }.status shouldBe HttpStatusCode.NoContent
            client.delete("/v1/campaigns/$campaignId/scenes/$id") { bearerAuth(tokenOf(gameMaster)) }.status shouldBe HttpStatusCode.NotFound
        }
    }
})
