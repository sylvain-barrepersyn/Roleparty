package app.roleparty.campaign.creature.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.contract.web.AbilityName
import app.roleparty.campaign.creature.contract.web.AbilityScoresBody
import app.roleparty.campaign.creature.contract.web.AlignmentName
import app.roleparty.campaign.creature.contract.web.ArmorTrainingName
import app.roleparty.campaign.creature.contract.web.BackgroundBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.ClassLevelBody
import app.roleparty.campaign.creature.contract.web.HitDieName
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterDetailsBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.creature.contract.web.SizeName
import app.roleparty.campaign.creature.contract.web.SkillName
import app.roleparty.campaign.creature.contract.web.SpeciesBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetUpdateRequestBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetResponseBody
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.infrastructure.adapter.primary.CreatureLocalization
import app.roleparty.campaign.creature.infrastructure.adapter.primary.toResponseBody
import app.roleparty.campaign.creature.test.CreatureFixture
import app.roleparty.campaign.creature.test.CreatureSamples
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
            CreatureError.GameMasterRequired, CreatureError.ParticipantRequired, CreatureError.PlayerCharacterOwnerRequired -> call.respond(HttpStatusCode.Forbidden)
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

    test("the game master should create a character from a monster, rename it with notes, then unlink it to change its stats") {
        testApplication {
            val fixtures = CreatureFixture()
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val dragonId = fixtures.`given a dragon sheet`(gameMaster, campaignId)
            application {
                install(ContentNegotiation) { json() }
                installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
                serveCreatureRoutes(fixtures.module, localization)
            }

            val created = client.post("/v1/campaigns/$campaignId/character-sheets") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody("""{"monsterSheetId":"$dragonId"}""")
            }
            created.status shouldBe HttpStatusCode.Created
            val id = Json.decodeFromString<CharacterSheetCreatedResponseBody>(created.bodyAsText()).id
            val dragon = CreatureSamples.dragon(campaignId, dragonId).toResponseBody().sheet

            client.put("/v1/campaigns/$campaignId/character-sheets/$id") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(CharacterSheetUpdateRequestBody("Ember", null, "Hoards gold.", null)))
            }.status shouldBe HttpStatusCode.NoContent

            client.put("/v1/campaigns/$campaignId/character-sheets/$id") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(CharacterSheetUpdateRequestBody("Ember", null, "", dragon)))
            }.status shouldBe HttpStatusCode.BadRequest

            val linked = Json.decodeFromString<CharacterSheetResponseBody>(
                client.get("/v1/campaigns/$campaignId/character-sheets/$id") { bearerAuth(tokenOf(gameMaster)) }.bodyAsText(),
            )
            linked shouldBe CharacterSheetResponseBody(id, dragonId, "Hoards gold.", dragon.copy(name = "Ember", imageId = null))

            client.post("/v1/campaigns/$campaignId/character-sheets/$id/unlink") { bearerAuth(tokenOf(gameMaster)) }.status shouldBe HttpStatusCode.NoContent
            client.put("/v1/campaigns/$campaignId/character-sheets/$id") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(CharacterSheetUpdateRequestBody("Ember", null, "", dragon.copy(passivePerception = 25))))
            }.status shouldBe HttpStatusCode.NoContent

            val listed = client.get("/v1/campaigns/$campaignId/character-sheets") { bearerAuth(tokenOf(gameMaster)) }
            Json.decodeFromString<List<CharacterSheetOverviewResponseBody>>(listed.bodyAsText()).single().monsterSheetId shouldBe null
            client.get("/v1/campaigns/$campaignId/character-sheets") { bearerAuth(tokenOf(AccountId.generate())) }.status shouldBe HttpStatusCode.Forbidden
        }
    }

    test("a player should create a character of any class, list it and read it, while other players cannot") {
        testApplication {
            val fixtures = CreatureFixture()
            val campaignId = fixtures.`given a campaign`()
            val player = fixtures.`given a player of`(campaignId)
            val other = fixtures.`given a player of`(campaignId)
            application {
                install(ContentNegotiation) { json() }
                installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
                serveCreatureRoutes(fixtures.module, localization)
            }
            val json = Json { ignoreUnknownKeys = true }

            val creation = PlayerCharacterCreationRequestBody(
                name = "Gimli",
                imageId = ImageId.generate(),
                className = "Runesmith",
                hitDie = HitDieName.D10,
                classFeatures = "Rune carving.",
                savingThrows = setOf(AbilityName.STRENGTH, AbilityName.CONSTITUTION),
                armorTrainings = setOf(ArmorTrainingName.LIGHT, ArmorTrainingName.SHIELDS),
                background = BackgroundBody("Mountain guide", "Tough", "Cartographer's tools"),
                skills = setOf(SkillName.ATHLETICS, SkillName.PERCEPTION),
                species = SpeciesBody("Stone dwarf", SizeName.MEDIUM, 30, "Darkvision."),
                languages = listOf("Common", "Dwarvish"),
                equipment = "Warhammer.",
                coins = 12,
                abilityScores = AbilityScoresBody(15, 13, 14, 10, 12, 8),
                alignment = AlignmentName.LAWFUL_GOOD,
                details = PlayerCharacterDetailsBody("Bearded.", "Stubborn.", "From the mountains.", ""),
            )
            val created = client.post("/v1/campaigns/$campaignId/player-characters") {
                bearerAuth(tokenOf(player))
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(creation))
            }
            created.status shouldBe HttpStatusCode.Created
            val characterId = json.decodeFromString<PlayerCharacterCreatedResponseBody>(created.bodyAsText()).id

            val listed = client.get("/v1/campaigns/$campaignId/player-characters") { bearerAuth(tokenOf(player)) }
            json.decodeFromString<List<PlayerCharacterOverviewResponseBody>>(listed.bodyAsText()).map { it.id } shouldBe listOf(characterId)

            val sheet = json.decodeFromString<PlayerCharacterResponseBody>(
                client.get("/v1/player-characters/$characterId") { bearerAuth(tokenOf(player)) }.bodyAsText()
            )
            sheet.classes shouldBe listOf(ClassLevelBody("Runesmith", 1, HitDieName.D10))
            sheet.maxHitPoints shouldBe 12
            sheet.passivePerception shouldBe 13
            sheet.background shouldBe creation.background

            client.get("/v1/player-characters/$characterId") { bearerAuth(tokenOf(other)) }.status shouldBe HttpStatusCode.Forbidden
        }
    }
})
