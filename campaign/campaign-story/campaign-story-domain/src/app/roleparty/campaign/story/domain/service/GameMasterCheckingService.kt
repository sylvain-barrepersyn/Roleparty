package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.domain.port.secondary.Campaigns

internal class GameMasterCheckingService(private val campaigns: Campaigns) {

    suspend fun check(requester: AccountId, campaignId: CampaignId): StoryError? {
        val gameMaster = campaigns.gameMasterOf(campaignId) ?: return StoryError.CampaignNotFound(campaignId)
        return if (gameMaster == requester) null else StoryError.GameMasterRequired
    }
}
