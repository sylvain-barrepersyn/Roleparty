package app.roleparty.campaign.tabletop.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.Tabletop

interface Tabletops {

    suspend fun save(tabletop: Tabletop)

    suspend fun ofCampaign(campaignId: CampaignId): Tabletop?
}
