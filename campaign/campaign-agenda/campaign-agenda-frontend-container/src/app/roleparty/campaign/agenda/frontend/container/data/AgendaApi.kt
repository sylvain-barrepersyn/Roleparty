package app.roleparty.campaign.agenda.frontend.container.data

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceRequestBody
import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResource
import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResponseBody
import app.roleparty.campaign.agenda.contract.web.AgendaResponseBody
import app.roleparty.campaign.agenda.contract.web.DigitalGamingTableRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionRenamingRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionScheduledResponseBody
import app.roleparty.campaign.agenda.contract.web.GameSessionSchedulingRequestBody
import app.roleparty.campaign.agenda.contract.web.GameSessionsResource
import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.campaign.agenda.contract.web.PresenceRequestBody
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

    suspend fun agenda(campaignId: CampaignId): Outcome<AgendaResponseBody, BackendFailure>

    suspend fun scheduleGameSession(campaignId: CampaignId, date: LocalDate): Outcome<GameSessionId, BackendFailure>

    suspend fun renameGameSession(campaignId: CampaignId, gameSessionId: GameSessionId, name: String): Outcome<Unit, BackendFailure>

    suspend fun changePresence(campaignId: CampaignId, gameSessionId: GameSessionId, participant: AccountId, presence: PresenceName): Outcome<Unit, BackendFailure>

    suspend fun changeDigitalGamingTable(campaignId: CampaignId, gameSessionId: GameSessionId, available: Boolean): Outcome<Unit, BackendFailure>

    suspend fun preference(campaignId: CampaignId): Outcome<AgendaPreferenceResponseBody, BackendFailure>

    suspend fun changePreference(campaignId: CampaignId, presence: PresenceName, digitalGamingTable: Boolean): Outcome<Unit, BackendFailure>
}

class HttpAgendaApi(private val client: HttpClient) : AgendaApi {

    override suspend fun agenda(campaignId: CampaignId) =
        backendCall<AgendaResponseBody> { client.get(GameSessionsResource(campaignId)) }

    override suspend fun scheduleGameSession(campaignId: CampaignId, date: LocalDate) =
        backendCall<GameSessionScheduledResponseBody> {
            client.post(GameSessionsResource(campaignId)) { jsonBody(GameSessionSchedulingRequestBody(date)) }
        }.map { it.id }

    override suspend fun renameGameSession(campaignId: CampaignId, gameSessionId: GameSessionId, name: String) =
        backendCall<Unit> {
            client.put(GameSessionsResource.Name(GameSessionsResource(campaignId), gameSessionId)) { jsonBody(GameSessionRenamingRequestBody(name)) }
        }

    override suspend fun changePresence(campaignId: CampaignId, gameSessionId: GameSessionId, participant: AccountId, presence: PresenceName) =
        backendCall<Unit> {
            client.put(GameSessionsResource.Presence(GameSessionsResource(campaignId), gameSessionId, participant)) { jsonBody(PresenceRequestBody(presence)) }
        }

    override suspend fun changeDigitalGamingTable(campaignId: CampaignId, gameSessionId: GameSessionId, available: Boolean) =
        backendCall<Unit> {
            client.put(GameSessionsResource.DigitalGamingTable(GameSessionsResource(campaignId), gameSessionId)) {
                jsonBody(DigitalGamingTableRequestBody(available))
            }
        }

    override suspend fun preference(campaignId: CampaignId) =
        backendCall<AgendaPreferenceResponseBody> { client.get(AgendaPreferenceResource(campaignId)) }

    override suspend fun changePreference(campaignId: CampaignId, presence: PresenceName, digitalGamingTable: Boolean) =
        backendCall<Unit> { client.put(AgendaPreferenceResource(campaignId)) { jsonBody(AgendaPreferenceRequestBody(presence, digitalGamingTable)) } }
}
