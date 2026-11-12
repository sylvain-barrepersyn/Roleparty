package app.roleparty.campaign.tabletop.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.contract.web.IceServerBody
import app.roleparty.campaign.tabletop.contract.web.PeerBody
import app.roleparty.campaign.tabletop.contract.web.SeatCommandBody
import app.roleparty.campaign.tabletop.contract.web.SeatEventBody
import app.roleparty.campaign.tabletop.contract.web.SeatResponseBody
import app.roleparty.campaign.tabletop.contract.web.SignalBody
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.campaign.tabletop.contract.web.TabletopEventBody
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.infrastructure.adapter.primary.TabletopLocalization
import app.roleparty.campaign.tabletop.test.TabletopFixture
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import app.roleparty.core.test.ControlledClock
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
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
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json

class TabletopRoutesTest : FunSpec({

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

    val localization = object : TabletopLocalization {
        override suspend fun respond(call: ApplicationCall, error: TabletopError) = when (error) {
            TabletopError.GameMasterRequired, TabletopError.ParticipantRequired -> call.respond(HttpStatusCode.Forbidden)
            TabletopError.GameSessionAlreadyStarted, TabletopError.GameSessionNotStarted -> call.respond(HttpStatusCode.Conflict)
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }

    fun ApplicationTestBuilder.serve(fixtures: TabletopFixture, env: Map<String, String> = emptyMap()): HttpClient {
        application {
            install(ContentNegotiation) { json() }
            installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
            serveTabletopRoutes(fixtures.module, localization, env)
        }
        return createClient { install(WebSockets) }
    }

    suspend fun HttpClient.start(requester: AccountId, path: String, gameSessionId: Any) = put(path) {
        bearerAuth(tokenOf(requester))
        contentType(ContentType.Application.Json)
        setBody("""{"gameSessionId":"$gameSessionId"}""")
    }.status

    suspend fun HttpClient.connect(requester: AccountId, path: String) = webSocketSession(path) { bearerAuth(tokenOf(requester)) }

    test("the game master should start a game session, announced to the participants watching the campaign") {
        testApplication {
            val fixtures = TabletopFixture(ControlledClock(Instant.parse("2026-11-12T19:00:00Z")))
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val gameSession = fixtures.`given a game session`(campaignId, name = "Into the mists")
            val client = serve(fixtures)
            val tabletop = "/v1/campaigns/$campaignId/tabletop"
            val started = StartedGameSessionBody(gameSession.id, gameSession.date, "Into the mists")

            val watching = client.connect(player, "$tabletop/events")
            watching.nextEvent<TabletopEventBody.Snapshot>().tabletop shouldBe TabletopResponseBody()

            client.start(player, tabletop, gameSession.id) shouldBe HttpStatusCode.Forbidden
            client.start(gameMaster, tabletop, gameSession.id) shouldBe HttpStatusCode.NoContent
            client.start(gameMaster, tabletop, gameSession.id) shouldBe HttpStatusCode.Conflict

            watching.nextEvent<TabletopEventBody.GameSessionStarted>().gameSession shouldBe started
            val read = client.get(tabletop) { bearerAuth(tokenOf(player)) }
            Json.decodeFromString<TabletopResponseBody>(read.bodyAsText()) shouldBe TabletopResponseBody(started)
            client.connect(gameMaster, "$tabletop/events").nextEvent<TabletopEventBody.Snapshot>().tabletop shouldBe TabletopResponseBody(started)
        }
    }

    test("the people at the table should exchange signals, and attend while they hear the game master") {
        testApplication {
            val fixtures = TabletopFixture(ControlledClock(Instant.parse("2026-11-12T19:00:00Z")))
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val gimli = fixtures.`given a player character`(campaignId, player, "Gimli")
            fixtures.`given a started game session`(gameMaster, campaignId)
            val client = serve(fixtures, mapOf("TABLETOP_ICE_SERVER_URLS" to "stun:stun.roleparty.app:3478, turn:turn.roleparty.app:3478"))
            val tabletop = "/v1/campaigns/$campaignId/tabletop"
            val master = AttendeeBody(gameMaster, gameMaster = true)
            val dwarf = AttendeeBody(player, gameMaster = false, characterName = "Gimli", imageId = gimli.imageId)

            val seat = client.get("$tabletop/seat?playerCharacterId=${gimli.id}") { bearerAuth(tokenOf(player)) }
            Json.decodeFromString<SeatResponseBody>(seat.bodyAsText()) shouldBe
                SeatResponseBody(player, gameMaster = false, listOf(IceServerBody(listOf("stun:stun.roleparty.app:3478", "turn:turn.roleparty.app:3478"))))

            val watching = client.connect(player, "$tabletop/events")
            watching.nextEvent<TabletopEventBody.Snapshot>().tabletop.attendees.shouldBeEmpty()

            val masterSeat = client.connect(gameMaster, "$tabletop/seat/signals")
            masterSeat.nextSeatEvent<SeatEventBody.Welcomed>().peers.shouldBeEmpty()
            masterSeat.attendees { it == listOf(master) }

            val playerSeat = client.connect(player, "$tabletop/seat/signals?playerCharacterId=${gimli.id}")
            playerSeat.nextSeatEvent<SeatEventBody.Welcomed>().peers shouldBe listOf(PeerBody(gameMaster, gameMaster = true))
            masterSeat.nextSeatEvent<SeatEventBody.PeerJoined>().peer shouldBe PeerBody(player, gameMaster = false)

            playerSeat.command(SeatCommandBody.Signal(gameMaster, SignalBody.Offer("v=0")))
            masterSeat.nextSeatEvent<SeatEventBody.Signal>() shouldBe SeatEventBody.Signal(player, SignalBody.Offer("v=0"))
            masterSeat.command(SeatCommandBody.Signal(player, SignalBody.IceCandidate("candidate:1", "0", 0)))
            playerSeat.nextSeatEvent<SeatEventBody.Signal>() shouldBe SeatEventBody.Signal(gameMaster, SignalBody.IceCandidate("candidate:1", "0", 0))

            playerSeat.command(SeatCommandBody.HearGameMaster(hearing = true))
            masterSeat.attendees { it == listOf(master, dwarf) }
            watching.watchedAttendees { it == listOf(master, dwarf) }

            playerSeat.command(SeatCommandBody.ChangeMedia(camera = true, microphone = false))
            masterSeat.attendees { it == listOf(master, dwarf.copy(camera = true)) }

            playerSeat.close(CloseReason(CloseReason.Codes.NORMAL, "Window closed"))
            masterSeat.nextSeatEvent<SeatEventBody.PeerLeft>().accountId shouldBe player
            masterSeat.attendees { it == listOf(master) }
            watching.watchedAttendees { it == listOf(master) }

            masterSeat.close(CloseReason(CloseReason.Codes.NORMAL, "Window closed"))
            watching.watchedAttendees { it.isEmpty() }
        }
    }

    test("a seat taken again from elsewhere should replace the previous one") {
        testApplication {
            val fixtures = TabletopFixture(ControlledClock())
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            fixtures.`given a started game session`(gameMaster, campaignId)
            val client = serve(fixtures)

            val first = client.connect(gameMaster, "/v1/campaigns/$campaignId/tabletop/seat/signals")
            first.nextSeatEvent<SeatEventBody.Welcomed>()
            val second = client.connect(gameMaster, "/v1/campaigns/$campaignId/tabletop/seat/signals")

            second.nextSeatEvent<SeatEventBody.Welcomed>().peers.shouldBeEmpty()
            first.closed().code shouldBe CloseReason.Codes.GOING_AWAY.code
        }
    }

    test("strangers, players without their character and tables that are not started should be refused") {
        testApplication {
            val fixtures = TabletopFixture(ControlledClock())
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val stranger = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val client = serve(fixtures)
            val tabletop = "/v1/campaigns/$campaignId/tabletop"

            client.get(tabletop) { bearerAuth(tokenOf(stranger)) }.status shouldBe HttpStatusCode.Forbidden
            client.get(tabletop).status shouldBe HttpStatusCode.Unauthorized
            client.connect(stranger, "$tabletop/events").closed().code shouldBe CloseReason.Codes.VIOLATED_POLICY.code
            client.get("$tabletop/seat") { bearerAuth(tokenOf(gameMaster)) }.status shouldBe HttpStatusCode.Conflict
            client.connect(gameMaster, "$tabletop/seat/signals").closed().code shouldBe CloseReason.Codes.VIOLATED_POLICY.code

            fixtures.`given a started game session`(gameMaster, campaignId)
            client.get("$tabletop/seat") { bearerAuth(tokenOf(player)) }.status shouldBe HttpStatusCode.BadRequest
            client.connect(player, "$tabletop/seat/signals").closed().code shouldBe CloseReason.Codes.VIOLATED_POLICY.code
            client.connect(player, "$tabletop/seat/signals?playerCharacterId=not-an-identifier").closed().code shouldBe CloseReason.Codes.CANNOT_ACCEPT.code
        }
    }
})

private val json = Json { ignoreUnknownKeys = true }

private suspend fun <T> patiently(block: suspend () -> T): T = withContext(Dispatchers.Default) { withTimeout(10.seconds) { block() } }

private suspend fun DefaultClientWebSocketSession.nextText(): String = patiently {
    var text: String? = null
    while (text == null) text = (incoming.receive() as? Frame.Text)?.readText()
    text
}

private suspend inline fun <reified T : TabletopEventBody> DefaultClientWebSocketSession.nextEvent(): T {
    while (true) (json.decodeFromString(TabletopEventBody.serializer(), nextText()) as? T)?.let { return it }
}

private suspend inline fun <reified T : SeatEventBody> DefaultClientWebSocketSession.nextSeatEvent(): T {
    while (true) (json.decodeFromString(SeatEventBody.serializer(), nextText()) as? T)?.let { return it }
}

private suspend fun DefaultClientWebSocketSession.attendees(expected: (List<AttendeeBody>) -> Boolean) {
    do {
        val attendees = nextSeatEvent<SeatEventBody.AttendeesChanged>().attendees
    } while (!expected(attendees))
}

private suspend fun DefaultClientWebSocketSession.watchedAttendees(expected: (List<AttendeeBody>) -> Boolean) {
    do {
        val attendees = nextEvent<TabletopEventBody.AttendeesChanged>().attendees
    } while (!expected(attendees))
}

private suspend fun DefaultClientWebSocketSession.command(command: SeatCommandBody) =
    send(Frame.Text(json.encodeToString(SeatCommandBody.serializer(), command)))

private suspend fun DefaultClientWebSocketSession.closed(): CloseReason = patiently { checkNotNull(closeReason.await()) }
