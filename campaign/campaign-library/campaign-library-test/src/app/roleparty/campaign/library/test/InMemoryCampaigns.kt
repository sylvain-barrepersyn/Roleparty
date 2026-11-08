package app.roleparty.campaign.library.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.domain.port.secondary.Campaigns

internal class InMemoryCampaigns : Campaigns {

    private val gameMasters = mutableMapOf<CampaignId, AccountId>()

    override suspend fun gameMasterOf(campaignId: CampaignId) = gameMasters[campaignId]

    fun given(campaignId: CampaignId, gameMaster: AccountId) {
        gameMasters[campaignId] = gameMaster
    }
}
