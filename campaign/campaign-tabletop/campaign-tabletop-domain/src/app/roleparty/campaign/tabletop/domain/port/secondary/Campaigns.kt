package app.roleparty.campaign.tabletop.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.CampaignParticipants

interface Campaigns {

    suspend fun participantsOf(campaignId: CampaignId): CampaignParticipants?
}
