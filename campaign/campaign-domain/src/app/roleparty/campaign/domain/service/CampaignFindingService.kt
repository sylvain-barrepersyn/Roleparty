package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.port.primary.FindCampaigns
import app.roleparty.campaign.domain.port.secondary.Campaigns

internal class CampaignFindingService(
    private val campaigns: Campaigns,
    private val access: CampaignAccessCheckingService,
) : FindCampaigns {

    override suspend fun masteredBy(accountId: AccountId) = campaigns.masteredBy(accountId)

    override suspend fun playedBy(accountId: AccountId) = campaigns.playedBy(accountId)

    override suspend fun byId(requester: AccountId, campaignId: CampaignId) = access.asParticipant(requester, campaignId)
}
