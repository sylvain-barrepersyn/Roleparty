package app.roleparty.campaign.agenda.infrastructure.adapter.primary

import app.roleparty.campaign.agenda.contract.web.GameSessionRenamingRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionScheduledResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionSchedulingRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionsResource
import app.roleparty.campaign.agenda.domain.AgendaModule
import app.roleparty.campaign.agenda.domain.model.AgendaError
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
            call.respondOutcome(agenda.findGameSessions.ofCampaign(call.user().accountId, resource.campaignId), localization) { sessions ->
                sessions.map { GameSessionResponseBody(it.id, it.date, it.name) }
            }
        }

        put<GameSessionsResource.Name> { resource ->
            val body = call.receive<GameSessionRenamingRequestBody>()
            call.respondStatus(agenda.renameGameSession(call.user().accountId, resource.parent.campaignId, resource.gameSessionId, body.name), localization)
        }
    }
}
