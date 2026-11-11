package app.roleparty.campaign.agenda.infrastructure.adapter.primary

import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceRequestBody
import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResource
import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResponseBody
import app.roleparty.campaign.agenda.contract.web.AgendaResponseBody
import app.roleparty.campaign.agenda.contract.web.DigitalGamingTableRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionRenamingRequestBody
import app.roleparty.campaign.agenda.contract.web.ParticipantBody
import app.roleparty.campaign.agenda.contract.web.PresenceBody
import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.campaign.agenda.contract.web.PresenceRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionScheduledResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionSchedulingRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionsResource
import app.roleparty.campaign.agenda.domain.AgendaModule
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.ktor.rest.respondStatus
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.Route

interface AgendaLocalization : ErrorResponder<AgendaError>

internal fun Route.agendaRoutes(agenda: AgendaModule, localization: AgendaLocalization) {
    authenticated {
        post<GameSessionsResource> { resource ->
            val body = call.receive<GameSessionSchedulingRequestBody>()
            call.respondOutcome(agenda.scheduleGameSession(call.user().accountId, resource.campaignId, body.date), localization, HttpStatusCode.Created) {
                GameSessionScheduledResponseBody(it)
            }
        }

        get<GameSessionsResource> { resource ->
            val requester = call.user().accountId
            call.respondOutcome(agenda.findAgenda.ofCampaign(requester, resource.campaignId), localization) { found ->
                AgendaResponseBody(
                    requester = requester,
                    participants = found.participants.map { ParticipantBody(it.accountId, it.gameMaster, it.characterNames) },
                    gameSessions = found.gameSessions.map { it.toResponseBody() },
                )
            }
        }

        put<GameSessionsResource.Name> { resource ->
            val body = call.receive<GameSessionRenamingRequestBody>()
            call.respondStatus(agenda.renameGameSession(call.user().accountId, resource.parent.campaignId, resource.gameSessionId, body.name), localization)
        }

        put<GameSessionsResource.Presence> { resource ->
            val presence = Presence.valueOf(call.receive<PresenceRequestBody>().presence.name)
            val outcome = agenda.changePresence(call.user().accountId, resource.parent.campaignId, resource.gameSessionId, resource.accountId, presence)
            call.respondStatus(outcome, localization)
        }

        put<GameSessionsResource.DigitalGamingTable> { resource ->
            val body = call.receive<DigitalGamingTableRequestBody>()
            call.respondStatus(agenda.changeDigitalGamingTable(call.user().accountId, resource.parent.campaignId, resource.gameSessionId, body.available), localization)
        }

        get<AgendaPreferenceResource> { resource ->
            call.respondOutcome(agenda.findAgendaPreference.ofRequester(call.user().accountId, resource.campaignId), localization) {
                AgendaPreferenceResponseBody(it.gameMaster, PresenceName.valueOf(it.preference.presence.name), it.preference.digitalGamingTable)
            }
        }

        put<AgendaPreferenceResource> { resource ->
            val body = call.receive<AgendaPreferenceRequestBody>()
            val preference = AgendaPreference(Presence.valueOf(body.presence.name), body.digitalGamingTable)
            call.respondStatus(agenda.changeAgendaPreference(call.user().accountId, resource.campaignId, preference), localization)
        }
    }
}

private fun GameSession.toResponseBody() = GameSessionResponseBody(
    id = id,
    date = date,
    name = name,
    digitalGamingTable = digitalGamingTable,
    presences = presences.map { PresenceBody(it.key, PresenceName.valueOf(it.value.name)) }.sortedBy { it.accountId.toString() },
)
