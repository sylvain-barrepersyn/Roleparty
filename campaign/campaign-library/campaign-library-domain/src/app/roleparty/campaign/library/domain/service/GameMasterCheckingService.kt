package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.secondary.Campaigns

internal class GameMasterCheckingService(private val campaigns: Campaigns) {

    suspend fun check(requester: AccountId, campaignId: CampaignId): LibraryError? {
        val gameMaster = campaigns.gameMasterOf(campaignId) ?: return LibraryError.CampaignNotFound(campaignId)
        return if (gameMaster == requester) null else LibraryError.GameMasterRequired
    }
}
