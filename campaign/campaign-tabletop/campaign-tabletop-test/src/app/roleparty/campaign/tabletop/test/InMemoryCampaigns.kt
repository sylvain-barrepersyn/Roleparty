package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.CampaignParticipants
import app.roleparty.campaign.tabletop.domain.port.secondary.Campaigns

internal class InMemoryCampaigns : Campaigns {

    private val participants = mutableMapOf<CampaignId, CampaignParticipants>()

    override suspend fun participantsOf(campaignId: CampaignId) = participants[campaignId]

    fun given(campaignId: CampaignId, campaignParticipants: CampaignParticipants) {
        participants[campaignId] = campaignParticipants
    }
}
