package app.roleparty.campaign.frontend.container.data

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.CampaignCreatedResponseBody
import app.roleparty.campaign.contract.web.CampaignCreationRequestBody
import app.roleparty.campaign.contract.web.CampaignJoinedResponseBody
import app.roleparty.campaign.contract.web.CampaignJoiningRequestBody
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.CampaignResponseBody
import app.roleparty.campaign.contract.web.CampaignsResource
import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.map
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.backendCall
import app.roleparty.core.ktor.client.jsonBody
import app.roleparty.media.commons.ImageId
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import kotlinx.datetime.LocalDate

interface CampaignApi {

    suspend fun campaigns(participation: Participation, today: LocalDate): Outcome<List<CampaignOverviewResponseBody>, BackendFailure>

    suspend fun campaign(campaignId: CampaignId): Outcome<CampaignResponseBody, BackendFailure>

    suspend fun createCampaign(name: String, description: String, imageId: ImageId): Outcome<CampaignId, BackendFailure>

    suspend fun joinCampaign(code: String): Outcome<CampaignId, BackendFailure>

    suspend fun invitations(campaignId: CampaignId): Outcome<List<InvitationResponseBody>, BackendFailure>

    suspend fun generateInvitation(campaignId: CampaignId): Outcome<InvitationResponseBody, BackendFailure>
}

class HttpCampaignApi(private val client: HttpClient) : CampaignApi {

    override suspend fun campaigns(participation: Participation, today: LocalDate) =
        backendCall<List<CampaignOverviewResponseBody>> { client.get(CampaignsResource(participation, today)) }

    override suspend fun campaign(campaignId: CampaignId) =
        backendCall<CampaignResponseBody> { client.get(campaignOf(campaignId)) }

    override suspend fun createCampaign(name: String, description: String, imageId: ImageId) =
        backendCall<CampaignCreatedResponseBody> {
            client.post(CampaignsResource()) { jsonBody(CampaignCreationRequestBody(name, description, imageId)) }
        }.map { it.id }

    override suspend fun joinCampaign(code: String) =
        backendCall<CampaignJoinedResponseBody> {
            client.post(CampaignsResource.Join()) { jsonBody(CampaignJoiningRequestBody(code)) }
        }.map { it.campaignId }

    override suspend fun invitations(campaignId: CampaignId) =
        backendCall<List<InvitationResponseBody>> { client.get(CampaignsResource.Campaign.Invitations(campaignOf(campaignId))) }

    override suspend fun generateInvitation(campaignId: CampaignId) =
        backendCall<InvitationResponseBody> { client.post(CampaignsResource.Campaign.Invitations(campaignOf(campaignId))) }

    private fun campaignOf(campaignId: CampaignId) = CampaignsResource.Campaign(campaignId)
}
