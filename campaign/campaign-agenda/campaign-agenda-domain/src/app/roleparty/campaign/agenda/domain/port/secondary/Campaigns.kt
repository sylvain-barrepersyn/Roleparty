package app.roleparty.campaign.agenda.domain.port.secondary

import app.roleparty.campaign.agenda.domain.model.CampaignParticipants
import app.roleparty.campaign.commons.CampaignId

interface Campaigns {

    suspend fun participantsOf(campaignId: CampaignId): CampaignParticipants?
}
