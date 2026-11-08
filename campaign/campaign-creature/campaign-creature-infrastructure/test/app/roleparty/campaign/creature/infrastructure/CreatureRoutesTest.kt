package app.roleparty.campaign.creature.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.contract.web.MonsterSheetCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetResponseBody
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.infrastructure.adapter.primary.CreatureLocalization
import app.roleparty.campaign.creature.infrastructure.adapter.primary.toResponseBody
import app.roleparty.campaign.creature.test.CreatureFixture
import app.roleparty.campaign.creature.test.CreatureSamples
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
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

class CreatureRoutesTest : FunSpec({

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

    val localization = object : CreatureLocalization {
        override suspend fun respond(call: ApplicationCall, error: CreatureError) = when (error) {
            CreatureError.GameMasterRequired -> call.respond(HttpStatusCode.Forbidden)
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }

    test("the game master should create, save and read a monster sheet that players cannot read") {
        testApplication {
            val fixtures = CreatureFixture()
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            application {
                install(ContentNegotiation) { json() }
                installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
                serveCreatureRoutes(fixtures.module, localization)
            }

            val created = client.post("/v1/campaigns/$campaignId/monster-sheets") { bearerAuth(tokenOf(gameMaster)) }
            created.status shouldBe HttpStatusCode.Created
            val id = Json.decodeFromString<MonsterSheetCreatedResponseBody>(created.bodyAsText()).id
            val dragon = CreatureSamples.dragon(campaignId, id).toResponseBody()

            client.put("/v1/campaigns/$campaignId/monster-sheets/$id") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(dragon.sheet))
            }.status shouldBe HttpStatusCode.NoContent

            val read = client.get("/v1/campaigns/$campaignId/monster-sheets/$id") { bearerAuth(tokenOf(gameMaster)) }
            Json.decodeFromString<MonsterSheetResponseBody>(read.bodyAsText()) shouldBe dragon

            client.get("/v1/campaigns/$campaignId/monster-sheets") { bearerAuth(tokenOf(AccountId.generate())) }.status shouldBe HttpStatusCode.Forbidden

            client.put("/v1/campaigns/$campaignId/monster-sheets/$id") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(dragon.sheet.copy(hitPoints = dragon.sheet.hitPoints.copy(dice = "lots"))))
            }.status shouldBe HttpStatusCode.BadRequest
        }
    }
})
