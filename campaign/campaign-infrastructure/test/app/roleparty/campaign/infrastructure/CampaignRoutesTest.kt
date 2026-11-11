package app.roleparty.campaign.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.contract.web.CampaignCreatedResponseBody
import app.roleparty.campaign.contract.web.CampaignCreationRequestBody
import app.roleparty.campaign.contract.web.CampaignJoiningRequestBody
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.infrastructure.adapter.primary.CampaignLocalization
import app.roleparty.campaign.test.CampaignFixture
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import app.roleparty.media.commons.ImageId
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
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
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

class CampaignRoutesTest : FunSpec({

    val keys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    val publicKey = keys.public as ECPublicKey
    val json = Json { ignoreUnknownKeys = true }

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

    val localization = object : CampaignLocalization {
        override suspend fun respond(call: ApplicationCall, error: CampaignError) = when (error) {
            is CampaignError.CampaignNotFound -> call.respond(HttpStatusCode.NotFound)
            CampaignError.GameMasterRequired, CampaignError.ParticipantRequired -> call.respond(HttpStatusCode.Forbidden)
            else -> call.respond(HttpStatusCode.BadRequest, error.toString())
        }
    }

    fun ApplicationTestBuilder.campaigns(): CampaignFixture {
        val fixtures = CampaignFixture()
        application {
            install(ContentNegotiation) { json() }
            installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
            serveCampaignRoutes(fixtures.module, localization)
        }
        return fixtures
    }

    suspend fun HttpClient.postJson(path: String, accountId: AccountId, body: String): HttpResponse = post(path) {
        bearerAuth(tokenOf(accountId))
        contentType(ContentType.Application.Json)
        setBody(body)
    }

    suspend fun HttpClient.getAs(path: String, accountId: AccountId) = get(path) { bearerAuth(tokenOf(accountId)) }

    test("campaigns should be listed with their next game session from the given day, nearest first") {
        testApplication {
            val fixtures = campaigns()
            val gameMaster = AccountId.generate()
            val unscheduled = fixtures.`given a campaign named`("Lost Mine", gameMaster)
            val scheduled = fixtures.`given a campaign named`("Tomb of Annihilation", gameMaster)
            fixtures.`given a game session of`(scheduled, LocalDate(2026, 11, 8))
            fixtures.`given a game session of`(scheduled, LocalDate(2026, 11, 12))

            val response = client.getAs("/v1/campaigns?participation=GAME_MASTER&today=2026-11-09", gameMaster)

            json.decodeFromString<List<CampaignOverviewResponseBody>>(response.bodyAsText()).map { it.id to it.nextSession } shouldBe listOf(
                scheduled to LocalDate(2026, 11, 12),
                unscheduled to null,
            )
        }
    }

    test("a game master should create a campaign that a player joins with an invitation code") {
        testApplication {
            campaigns()
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()

            val created = client.postJson("/v1/campaigns", gameMaster, json.encodeToString(CampaignCreationRequestBody("The Lost Mine", "", ImageId.generate())))
            created.status shouldBe HttpStatusCode.Created
            val campaignId = json.decodeFromString<CampaignCreatedResponseBody>(created.bodyAsText()).id

            val invitation = client.postJson("/v1/campaigns/$campaignId/invitations", gameMaster, "")
            invitation.status shouldBe HttpStatusCode.Created
            val code = json.decodeFromString<InvitationResponseBody>(invitation.bodyAsText()).code

            client.postJson("/v1/campaigns/join", player, json.encodeToString(CampaignJoiningRequestBody(code))).status shouldBe HttpStatusCode.OK
            val played = json.decodeFromString<List<CampaignOverviewResponseBody>>(client.getAs("/v1/campaigns?participation=PLAYER", player).bodyAsText())
            played.map { it.id } shouldBe listOf(campaignId)
        }
    }

    test("an unauthenticated request should be refused, as well as a malformed identifier") {
        testApplication {
            campaigns()

            client.get("/v1/campaigns").status shouldBe HttpStatusCode.Unauthorized
            client.getAs("/v1/campaigns/not-an-id", AccountId.generate()).status shouldBe HttpStatusCode.BadRequest
        }
    }
})
