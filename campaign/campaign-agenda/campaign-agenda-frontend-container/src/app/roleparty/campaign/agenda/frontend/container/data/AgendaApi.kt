package app.roleparty.campaign.agenda.frontend.container.data

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.GameSessionRenamingRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionScheduledResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionSchedulingRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionsResource
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.map
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.backendCall
import app.roleparty.core.ktor.client.jsonBody
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import kotlinx.datetime.LocalDate

interface AgendaApi {

    suspend fun gameSessions(campaignId: CampaignId): Outcome<List<GameSessionResponseBody>, BackendFailure>

    suspend fun scheduleGameSession(campaignId: CampaignId, date: LocalDate): Outcome<GameSessionId, BackendFailure>

    suspend fun renameGameSession(campaignId: CampaignId, gameSessionId: GameSessionId, name: String): Outcome<Unit, BackendFailure>
}

class HttpAgendaApi(private val client: HttpClient) : AgendaApi {

    override suspend fun gameSessions(campaignId: CampaignId) =
        backendCall<List<GameSessionResponseBody>> { client.get(GameSessionsResource(campaignId)) }

    override suspend fun scheduleGameSession(campaignId: CampaignId, date: LocalDate) =
        backendCall<GameSessionScheduledResponseBody> {
            client.post(GameSessionsResource(campaignId)) { jsonBody(GameSessionSchedulingRequestBody(date)) }
        }.map { it.id }

    override suspend fun renameGameSession(campaignId: CampaignId, gameSessionId: GameSessionId, name: String) =
        backendCall<Unit> {
            client.put(GameSessionsResource.Name(GameSessionsResource(campaignId), gameSessionId)) { jsonBody(GameSessionRenamingRequestBody(name)) }
        }
}
