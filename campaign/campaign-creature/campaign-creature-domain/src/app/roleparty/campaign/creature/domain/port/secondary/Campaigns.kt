package app.roleparty.campaign.creature.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId

interface Campaigns {

    suspend fun gameMasterOf(campaignId: CampaignId): AccountId?
}
