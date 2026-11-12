package app.roleparty.campaign.tabletop.frontend.container.data

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.contract.web.SeatCommandBody
import app.roleparty.campaign.tabletop.contract.web.SeatEventBody
import app.roleparty.campaign.tabletop.contract.web.SeatResponseBody
import app.roleparty.campaign.tabletop.contract.web.TabletopEventBody
import app.roleparty.campaign.tabletop.contract.web.TabletopResource
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.contract.web.TabletopStartingRequestBody
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.backendCall
import app.roleparty.core.ktor.client.jsonBody
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.put
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

interface SeatSignals {

    val events: Flow<SeatEventBody>

    suspend fun send(command: SeatCommandBody)
}

interface TabletopApi {

    suspend fun tabletop(campaignId: CampaignId): Outcome<TabletopResponseBody, BackendFailure>

    suspend fun startGameSession(campaignId: CampaignId, gameSessionId: GameSessionId): Outcome<Unit, BackendFailure>

    fun events(campaignId: CampaignId): Flow<TabletopEventBody>

    suspend fun seat(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?): Outcome<SeatResponseBody, BackendFailure>

    suspend fun signal(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?, exchange: suspend (SeatSignals) -> Unit)
}

class HttpTabletopApi(private val client: HttpClient, private val baseUrl: String) : TabletopApi {

    private val json = Json { ignoreUnknownKeys = true }

    private val resources = ResourcesFormat()

    override suspend fun tabletop(campaignId: CampaignId) =
        backendCall<TabletopResponseBody> { client.get(TabletopResource(campaignId)) }

    override suspend fun startGameSession(campaignId: CampaignId, gameSessionId: GameSessionId) =
        backendCall<Unit> { client.put(TabletopResource(campaignId)) { jsonBody(TabletopStartingRequestBody(gameSessionId)) } }

    override fun events(campaignId: CampaignId): Flow<TabletopEventBody> = channelFlow {
        untilDisconnected {
            client.webSocket(urlString = socketUrl(href(resources, TabletopResource.Events(TabletopResource(campaignId))))) {
                for (frame in incoming) frame.decode(TabletopEventBody.serializer())?.let { this@channelFlow.send(it) }
            }
        }
    }

    override suspend fun seat(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?) =
        backendCall<SeatResponseBody> { client.get(TabletopResource.Seat(TabletopResource(campaignId), playerCharacterId)) }

    override suspend fun signal(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?, exchange: suspend (SeatSignals) -> Unit) {
        val resource = TabletopResource.Seat.Signals(TabletopResource.Seat(TabletopResource(campaignId), playerCharacterId))
        untilDisconnected {
            client.webSocket(urlString = socketUrl(href(resources, resource))) {
                val session = this
                exchange(object : SeatSignals {
                    override val events = incoming.receiveAsFlow().mapNotNull { it.decode(SeatEventBody.serializer()) }

                    override suspend fun send(command: SeatCommandBody) =
                        session.send(Frame.Text(json.encodeToString(SeatCommandBody.serializer(), command)))
                })
            }
        }
    }

    private fun socketUrl(path: String) = baseUrl.replaceFirst("http", "ws") + path

    private fun <T> Frame.decode(deserializer: DeserializationStrategy<T>): T? =
        try {
            (this as? Frame.Text)?.let { json.decodeFromString(deserializer, it.readText()) }
        } catch (_: SerializationException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }

    private suspend fun untilDisconnected(block: suspend () -> Unit) {
        try {
            block()
        } catch (_: CancellationException) {
            currentCoroutineContext().ensureActive()
        } catch (_: Exception) {
            return
        }
    }
}
