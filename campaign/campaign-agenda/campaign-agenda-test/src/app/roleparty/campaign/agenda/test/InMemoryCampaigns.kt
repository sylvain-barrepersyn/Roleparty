package app.roleparty.campaign.agenda.test

import app.roleparty.campaign.agenda.domain.model.CampaignParticipants
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.commons.CampaignId

internal class InMemoryCampaigns : Campaigns {

    private val participants = mutableMapOf<CampaignId, CampaignParticipants>()

    override suspend fun participantsOf(campaignId: CampaignId) = participants[campaignId]

    fun given(campaignId: CampaignId, campaignParticipants: CampaignParticipants) {
        participants[campaignId] = campaignParticipants
    }
}
