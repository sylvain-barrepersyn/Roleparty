package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.port.secondary.Campaigns

internal class GameMasterCheckingService(private val campaigns: Campaigns) {

    suspend fun check(requester: AccountId, campaignId: CampaignId): CreatureError? {
        val gameMaster = campaigns.gameMasterOf(campaignId) ?: return CreatureError.CampaignNotFound(campaignId)
        return if (gameMaster == requester) null else CreatureError.GameMasterRequired
    }
}
