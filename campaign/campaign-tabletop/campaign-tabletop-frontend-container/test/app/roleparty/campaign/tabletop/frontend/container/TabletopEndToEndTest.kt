package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.campaign.tabletop.contract.web.TabletopEventBody
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.frontend.container.data.HttpTabletopApi
import app.roleparty.campaign.tabletop.frontend.container.data.TableStatus
import app.roleparty.campaign.tabletop.frontend.container.data.WebRtcTabletopCall
import app.roleparty.campaign.tabletop.infrastructure.adapter.primary.TabletopLocalization
import app.roleparty.campaign.tabletop.infrastructure.serveTabletopRoutes
import app.roleparty.campaign.tabletop.test.TabletopFixture
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.installBackend
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import app.roleparty.core.test.ControlledClock
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import java.net.ServerSocket
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable

class TabletopEndToEndTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val keys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    private val publicKey = keys.public as ECPublicKey
    private val port = ServerSocket(0).use { it.localPort }
    private val fixtures = TabletopFixture(ControlledClock())
    private val clients = mutableListOf<HttpClient>()
    private val background = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val gameMaster = AccountId.generate()
    private val player = AccountId.generate()
    private val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
    private val gimli = fixtures.`given a player character`(campaignId, player, "Gimli")
    private val gameSession = fixtures.`given a game session`(campaignId, name = "Into the mists")
    private val master = AttendeeBody(gameMaster, gameMaster = true)
    private val dwarf = AttendeeBody(player, gameMaster = false, characterName = "Gimli", imageId = gimli.imageId)

    private val localization = object : TabletopLocalization {
        override suspend fun respond(call: ApplicationCall, error: TabletopError) =
            call.respond(HttpStatusCode.Conflict, Refusal(error::class.simpleName.orEmpty(), "Refused"))
    }

    private val server = embeddedServer(Netty, port = port) {
        install(ContentNegotiation) { json() }
        installAuthentication(mapOf("JWT_PUBLIC_KEY" to Base64.encode(publicKey.encoded)))
        serveTabletopRoutes(fixtures.module, localization, mapOf("TABLETOP_ICE_SERVER_URLS" to ""))
    }.start(wait = false)

    init {
        afterTest {
            background.cancel()
            clients.forEach { it.close() }
            server.stop(0, 0)
        }

        test("the people of a campaign should meet at the table of a started game session") {
            withContext(Dispatchers.Default) {
                val masterApi = apiOf(gameMaster)
                val playerApi = apiOf(player)
                val watched = CopyOnWriteArrayList<TabletopEventBody>()
                background.launch { playerApi.events(campaignId).collect { watched += it } }
                patiently { watched.contains(TabletopEventBody.Snapshot(TabletopResponseBody())) }

                masterApi.startGameSession(campaignId, gameSession.id).shouldBeSuccess()
                val started = StartedGameSessionBody(gameSession.id, gameSession.date, "Into the mists")
                patiently { watched.contains(TabletopEventBody.GameSessionStarted(started)) }

                val camera = PaintedCamera()
                val masterCall = callOf(masterApi, null)
                masterCall.attended(master)
                val playerCall = callOf(playerApi, gimli.id, camera)
                masterCall.attended(master, dwarf)
                playerCall.attended(master, dwarf)
                patiently { watched.contains(TabletopEventBody.AttendeesChanged(listOf(master, dwarf))) }
                masterApi.tabletop(campaignId).shouldBeSuccess() shouldBe TabletopResponseBody(started, listOf(master, dwarf))

                playerCall.turnCamera(on = true) shouldBe true
                playerCall.turnMicrophone(on = true) shouldBe true
                masterCall.attended(master, dwarf.copy(camera = true, microphone = true))
                val painting = background.launch {
                    while (true) {
                        camera.paintRed()
                        delay(50.milliseconds)
                    }
                }
                withTimeout(20.seconds) {
                    masterCall.video(player).first { it != null }
                    playerCall.video(player).first { it != null }
                }
                painting.cancel()

                playerCall.turnCamera(on = false) shouldBe true
                masterCall.attended(master, dwarf.copy(microphone = true))
                withTimeout(10.seconds) { masterCall.video(player).first { it == null } }
                playerCall.video(player).value.shouldBeNull()
                camera.available = false
                playerCall.turnCamera(on = true) shouldBe false
                camera.opened shouldBe 1

                playerCall.close()
                masterCall.attended(master)
                patiently { watched.last() == TabletopEventBody.AttendeesChanged(listOf(master)) }
                masterCall.close()
                patiently { watched.last() == TabletopEventBody.AttendeesChanged(emptyList()) }
            }
        }

        test("the players should stop attending when the game master leaves, until they come back") {
            withContext(Dispatchers.Default) {
                fixtures.startGameSession(gameMaster, campaignId, gameSession.id).shouldBeSuccess()
                val masterCall = callOf(apiOf(gameMaster), null)
                val playerCall = callOf(apiOf(player), gimli.id)
                playerCall.attended(master, dwarf)

                masterCall.close()
                playerCall.attended()

                val back = callOf(apiOf(gameMaster), null)
                playerCall.attended(master, dwarf)
                back.attended(master, dwarf)

                back.close()
                playerCall.close()
            }
        }

        test("a seat should be refused before any game session is started, to strangers and without a character") {
            withContext(Dispatchers.Default) {
                callOf(apiOf(gameMaster), null).refused() shouldBe BackendFailure.Rejected("GameSessionNotStarted", "Refused")

                fixtures.startGameSession(gameMaster, campaignId, gameSession.id).shouldBeSuccess()
                callOf(apiOf(AccountId.generate()), null).refused() shouldBe BackendFailure.Rejected("ParticipantRequired", "Refused")
                callOf(apiOf(player), null).refused() shouldBe BackendFailure.Rejected("PlayerCharacterRequired", "Refused")
                apiOf(player).startGameSession(campaignId, gameSession.id) shouldBeFailure BackendFailure.Rejected("GameMasterRequired", "Refused")
                patiently { apiOf(AccountId.generate()).events(campaignId).first { false }.let { true } }.shouldBeNull()
            }
        }
    }

    private fun tokenOf(accountId: AccountId): String {
        val now = Clock.System.now()
        return JWT.create()
            .withKeyId(AccessTokenKeys.idOf(publicKey))
            .withIssuer(AccessTokenKeys.ISSUER)
            .withSubject(accountId.toString())
            .withIssuedAt(now.toJavaInstant())
            .withExpiresAt((now + 15.minutes).toJavaInstant())
            .sign(Algorithm.ECDSA256(publicKey, keys.private as ECPrivateKey))
    }

    private fun apiOf(accountId: AccountId): HttpTabletopApi {
        val url = "http://localhost:$port"
        val client = HttpClient(CIO) {
            installBackend(url)
            defaultRequest { bearerAuth(tokenOf(accountId)) }
        }
        clients += client
        return HttpTabletopApi(client, url)
    }

    private fun callOf(api: HttpTabletopApi, playerCharacterId: PlayerCharacterId?, camera: PaintedCamera = PaintedCamera()) =
        WebRtcTabletopCall(api, campaignId, playerCharacterId, { headlessMedia(camera) }, Dispatchers.Default, 200.milliseconds)

    private suspend fun WebRtcTabletopCall.attended(vararg attendees: AttendeeBody) {
        val reached = withTimeout(30.seconds) { status.first { it is TableStatus.Refused || (it as? TableStatus.Joined)?.attendees == attendees.toList() } }
        check(reached is TableStatus.Joined) { "The seat was refused: $reached" }
    }

    private suspend fun WebRtcTabletopCall.refused(): BackendFailure =
        withTimeout(30.seconds) { (status.first { it is TableStatus.Refused } as TableStatus.Refused).failure }.also { close() }

    private suspend fun <T> patiently(check: suspend () -> T): T? = withTimeout(30.seconds) {
        var result: T? = null
        while (result == null || result == false) {
            result = try {
                check()
            } catch (_: NoSuchElementException) {
                return@withTimeout null
            }
            if (result == null || result == false) delay(20.milliseconds)
        }
        result
    }

    @Serializable
    private data class Refusal(val code: String, val message: String)
}
