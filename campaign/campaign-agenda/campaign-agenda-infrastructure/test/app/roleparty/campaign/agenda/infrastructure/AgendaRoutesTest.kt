package app.roleparty.campaign.agenda.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionScheduledResponseBody
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.infrastructure.adapter.primary.AgendaLocalization
import app.roleparty.campaign.agenda.test.AgendaFixture
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import app.roleparty.core.test.ControlledClock
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
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlinx.serialization.json.Json

class AgendaRoutesTest : FunSpec({

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

    val localization = object : AgendaLocalization {
        override suspend fun respond(call: ApplicationCall, error: AgendaError) = when (error) {
            AgendaError.GameMasterRequired, AgendaError.ParticipantRequired -> call.respond(HttpStatusCode.Forbidden)
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }

    test("the game master should schedule and name a session that players can read but not change") {
        testApplication {
            val fixtures = AgendaFixture(ControlledClock(Instant.parse("2026-11-07T12:00:00Z")))
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            application {
                install(ContentNegotiation) { json() }
                installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
                serveAgendaRoutes(fixtures.module, localization)
            }

            val scheduled = client.post("/v1/campaigns/$campaignId/game-sessions") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody("""{"date":"2026-12-01"}""")
            }
            scheduled.status shouldBe HttpStatusCode.Created
            val id = Json.decodeFromString<GameSessionScheduledResponseBody>(scheduled.bodyAsText()).id

            client.put("/v1/campaigns/$campaignId/game-sessions/$id/name") {
                bearerAuth(tokenOf(gameMaster))
                contentType(ContentType.Application.Json)
                setBody("""{"name":"Session zero"}""")
            }.status shouldBe HttpStatusCode.NoContent

            client.put("/v1/campaigns/$campaignId/game-sessions/$id/name") {
                bearerAuth(tokenOf(player))
                contentType(ContentType.Application.Json)
                setBody("""{"name":"Mine"}""")
            }.status shouldBe HttpStatusCode.Forbidden

            val sessions = client.get("/v1/campaigns/$campaignId/game-sessions") { bearerAuth(tokenOf(player)) }
            Json.decodeFromString<List<GameSessionResponseBody>>(sessions.bodyAsText()).single().name shouldBe "Session zero"
        }
    }
})
