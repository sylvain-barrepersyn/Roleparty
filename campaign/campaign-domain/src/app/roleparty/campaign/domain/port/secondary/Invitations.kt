package app.roleparty.campaign.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Invitation

interface Invitations {

    suspend fun create(invitation: Invitation)

    suspend fun findByCode(code: Invitation.Code): Invitation?

    suspend fun ofCampaign(campaignId: CampaignId): List<Invitation>
}
