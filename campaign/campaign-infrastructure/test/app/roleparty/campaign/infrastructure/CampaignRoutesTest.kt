package app.roleparty.campaign.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.contract.web.AbilityScoresBody
import app.roleparty.campaign.contract.web.AlignmentName
import app.roleparty.campaign.contract.web.BackgroundName
import app.roleparty.campaign.contract.web.CampaignCreatedResponseBody
import app.roleparty.campaign.contract.web.CampaignCreationRequestBody
import app.roleparty.campaign.contract.web.CampaignJoiningRequestBody
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.contract.web.LanguageName
import app.roleparty.campaign.contract.web.LevelUpRequestBody
import app.roleparty.campaign.contract.web.PlayableClassName
import app.roleparty.campaign.contract.web.PlayerCharacterCreatedResponseBody
import app.roleparty.campaign.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.contract.web.SkillName
import app.roleparty.campaign.contract.web.SpeciesName
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

    fun ApplicationTestBuilder.campaigns() {
        val fixtures = CampaignFixture()
        application {
            install(ContentNegotiation) { json() }
            installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
            serveCampaignRoutes(fixtures.module, localization)
        }
    }

    suspend fun HttpClient.postJson(path: String, accountId: AccountId, body: String): HttpResponse = post(path) {
        bearerAuth(tokenOf(accountId))
        contentType(ContentType.Application.Json)
        setBody(body)
    }

    suspend fun HttpClient.getAs(path: String, accountId: AccountId) = get(path) { bearerAuth(tokenOf(accountId)) }

    test("a game master should create a campaign that a player joins with an invitation code, then creates and levels up a character") {
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

            val creation = PlayerCharacterCreationRequestBody(
                name = "Gimli",
                imageId = ImageId.generate(),
                characterClass = PlayableClassName.FIGHTER,
                species = SpeciesName.DWARF,
                background = BackgroundName.ARTISAN,
                backgroundTool = "SMITHS_TOOLS",
                alignment = AlignmentName.LAWFUL_GOOD,
                abilityScores = AbilityScoresBody(15, 13, 14, 10, 12, 8),
                appearance = "",
                backstory = "",
                skills = setOf(SkillName.ATHLETICS, SkillName.PERCEPTION),
                languages = setOf(LanguageName.COMMON, LanguageName.DWARVISH),
            )
            val character = client.postJson("/v1/campaigns/$campaignId/player-characters", player, json.encodeToString(creation))
            character.status shouldBe HttpStatusCode.Created
            val characterId = json.decodeFromString<PlayerCharacterCreatedResponseBody>(character.bodyAsText()).id

            val leveledUp = client.postJson("/v1/player-characters/$characterId/level-up", player, json.encodeToString(LevelUpRequestBody(PlayableClassName.FIGHTER)))
            leveledUp.status shouldBe HttpStatusCode.OK
            val sheet = json.decodeFromString<PlayerCharacterResponseBody>(leveledUp.bodyAsText())
            sheet.level shouldBe 2
            sheet.backgroundTool shouldBe "SMITHS_TOOLS"
            sheet.skills shouldBe setOf(SkillName.ATHLETICS, SkillName.PERCEPTION, SkillName.INVESTIGATION, SkillName.PERSUASION)
        }
    }

    test("an unauthenticated request should be refused, as well as a malformed identifier") {
        testApplication {
            campaigns()

            client.get("/v1/campaigns").status shouldBe HttpStatusCode.Unauthorized
            client.getAs("/v1/campaigns/not-an-id", AccountId.generate()).status shouldBe HttpStatusCode.BadRequest
        }
    }

    test("a background tool that does not fit the background should be refused") {
        testApplication {
            campaigns()
            val gameMaster = AccountId.generate()
            val created = client.postJson("/v1/campaigns", gameMaster, json.encodeToString(CampaignCreationRequestBody("The Lost Mine", "", ImageId.generate())))
            val campaignId = json.decodeFromString<CampaignCreatedResponseBody>(created.bodyAsText()).id

            val body = """{"name":"Gimli","imageId":"${ImageId.generate()}","characterClass":"FIGHTER","species":"DWARF","background":"ARTISAN",
                |"backgroundTool":"PAN_FLUTE","alignment":"NEUTRAL","abilityScores":{"strength":10,"dexterity":10,"constitution":10,"intelligence":10,"wisdom":10,"charisma":10},
                |"appearance":"","backstory":"","skills":["ATHLETICS","STEALTH"],"languages":["COMMON"]}""".trimMargin()

            client.postJson("/v1/campaigns/$campaignId/player-characters", gameMaster, body).status shouldBe HttpStatusCode.BadRequest
        }
    }
})
