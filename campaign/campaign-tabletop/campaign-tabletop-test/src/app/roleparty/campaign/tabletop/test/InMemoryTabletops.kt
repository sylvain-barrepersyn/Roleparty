package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.Tabletop
import app.roleparty.campaign.tabletop.domain.port.secondary.Tabletops

internal class InMemoryTabletops : Tabletops {

    private val tabletops = mutableMapOf<CampaignId, Tabletop>()

    override suspend fun save(tabletop: Tabletop) {
        tabletops[tabletop.campaignId] = tabletop
    }

    override suspend fun ofCampaign(campaignId: CampaignId) = tabletops[campaignId]
}
