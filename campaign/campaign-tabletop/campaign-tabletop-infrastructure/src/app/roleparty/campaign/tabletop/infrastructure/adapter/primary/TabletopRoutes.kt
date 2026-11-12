package app.roleparty.campaign.tabletop.infrastructure.adapter.primary

import app.roleparty.campaign.tabletop.contract.web.IceServerBody
import app.roleparty.campaign.tabletop.contract.web.SeatCommandBody
import app.roleparty.campaign.tabletop.contract.web.SeatEventBody
import app.roleparty.campaign.tabletop.contract.web.SeatResponseBody
import app.roleparty.campaign.tabletop.contract.web.TabletopEventBody
import app.roleparty.campaign.tabletop.contract.web.TabletopResource
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.contract.web.TabletopStartingRequestBody
import app.roleparty.campaign.tabletop.domain.TabletopModule
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.ktor.rest.respondStatus
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

interface TabletopLocalization : ErrorResponder<TabletopError>

private val json = Json { ignoreUnknownKeys = true }

private val REFUSED = CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Refused")
private val REPLACED = CloseReason(CloseReason.Codes.GOING_AWAY, "Seat taken from elsewhere")

internal fun Route.tabletopRoutes(tabletop: TabletopModule, hub: TabletopHub, iceServers: List<IceServerBody>, localization: TabletopLocalization) {
    authenticated {
        get<TabletopResource> { resource ->
            call.respondOutcome(tabletop.findStartedGameSession.ofCampaign(call.user().accountId, resource.campaignId), localization) { started ->
                TabletopResponseBody(started?.toBody(), hub.room(resource.campaignId).currentAttendees.map { it.toBody() })
            }
        }

        put<TabletopResource> { resource ->
            val body = call.receive<TabletopStartingRequestBody>()
            val outcome = tabletop.startGameSession(call.user().accountId, resource.campaignId, body.gameSessionId)
            if (outcome is Outcome.Success) hub.room(resource.campaignId).announce(outcome.value)
            call.respondStatus(outcome, localization)
        }

        get<TabletopResource.Seat> { resource ->
            call.respondOutcome(tabletop.takeSeat(call.user().accountId, resource.parent.campaignId, resource.playerCharacterId), localization) { seat ->
                SeatResponseBody(seat.accountId, seat.gameMaster, iceServers)
            }
        }

        webSocketResource<TabletopResource.Events> { resource ->
            val campaignId = resource.parent.campaignId
            val requester = call.user().accountId
            if (tabletop.findStartedGameSession.ofCampaign(requester, campaignId) is Outcome.Failure) return@webSocketResource close(REFUSED)

            val room = hub.room(campaignId)
            var attendees: Job? = null
            val starts = launch {
                room.starts
                    .onSubscription {
                        val started = (tabletop.findStartedGameSession.ofCampaign(requester, campaignId) as? Outcome.Success)?.value
                        send(TabletopEventBody.Snapshot(TabletopResponseBody(started?.toBody(), room.currentAttendees.map { it.toBody() })))
                        attendees = launch {
                            room.attendees.collect { present -> send(TabletopEventBody.AttendeesChanged(present.map { it.toBody() })) }
                        }
                    }
                    .collect { send(TabletopEventBody.GameSessionStarted(it.toBody())) }
            }
            try {
                incoming.consumeEach { }
            } finally {
                starts.cancel()
                attendees?.cancel()
            }
        }

        webSocketResource<TabletopResource.Seat.Signals> { resource ->
            val campaignId = resource.parent.parent.campaignId
            val seat = when (val outcome = tabletop.takeSeat(call.user().accountId, campaignId, resource.parent.playerCharacterId)) {
                is Outcome.Failure -> return@webSocketResource close(REFUSED)
                is Outcome.Success -> outcome.value
            }

            val room = hub.room(campaignId)
            val link = room.sit(seat)
            val writer = launch {
                for (event in link.events) send(event)
                close(REPLACED)
            }
            val attendees = launch {
                room.attendees.collect { present -> link.events.trySend(SeatEventBody.AttendeesChanged(present.map { it.toBody() })) }
            }
            try {
                for (frame in incoming) {
                    val command = (frame as? Frame.Text)?.readText()?.toCommand() ?: continue
                    room.handle(link, command)
                }
            } finally {
                attendees.cancel()
                withContext(NonCancellable) { room.leave(link) }
                writer.cancel()
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.send(event: TabletopEventBody) =
    send(Frame.Text(json.encodeToString(TabletopEventBody.serializer(), event)))

private suspend fun DefaultWebSocketServerSession.send(event: SeatEventBody) =
    send(Frame.Text(json.encodeToString(SeatEventBody.serializer(), event)))

private fun String.toCommand(): SeatCommandBody? =
    try {
        json.decodeFromString(SeatCommandBody.serializer(), this)
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
