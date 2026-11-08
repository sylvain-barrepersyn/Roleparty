package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.core.commons.Outcome

internal class CampaignAccessCheckingService(private val campaigns: Campaigns) {

    suspend fun asGameMaster(requester: AccountId, campaignId: CampaignId): Outcome<Campaign, CampaignError> {
        val campaign = campaigns.findById(campaignId) ?: return Outcome.Failure(CampaignError.CampaignNotFound(campaignId))
        if (!campaign.isGameMaster(requester)) return Outcome.Failure(CampaignError.GameMasterRequired)
        return Outcome.Success(campaign)
    }

    suspend fun asParticipant(requester: AccountId, campaignId: CampaignId): Outcome<Campaign, CampaignError> {
        val campaign = campaigns.findById(campaignId) ?: return Outcome.Failure(CampaignError.CampaignNotFound(campaignId))
        if (requester !in campaign) return Outcome.Failure(CampaignError.ParticipantRequired)
        return Outcome.Success(campaign)
    }
}
