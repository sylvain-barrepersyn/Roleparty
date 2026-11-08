package app.roleparty.campaign.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.campaign.domain.model.Invitation

interface Campaigns {

    suspend fun create(campaign: Campaign)

    suspend fun findById(campaignId: CampaignId): Campaign?

    suspend fun masteredBy(accountId: AccountId): List<CampaignOverview>

    suspend fun playedBy(accountId: AccountId): List<CampaignOverview>

    suspend fun addPlayer(campaignId: CampaignId, player: AccountId, redeemed: Invitation.Code): Boolean
}
