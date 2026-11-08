package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.domain.port.secondary.Campaigns

internal class InMemoryCampaigns(private val invitations: InMemoryInvitations) : Campaigns {

    private val campaigns = linkedMapOf<CampaignId, Campaign>()

    override suspend fun create(campaign: Campaign) {
        campaigns[campaign.id] = campaign
    }

    override suspend fun findById(campaignId: CampaignId) = campaigns[campaignId]

    override suspend fun masteredBy(accountId: AccountId) = campaigns.values.filter { it.gameMaster == accountId }.map { it.overview() }

    override suspend fun playedBy(accountId: AccountId) = campaigns.values.filter { accountId in it.players }.map { it.overview() }

    override suspend fun addPlayer(campaignId: CampaignId, player: AccountId, redeemed: Invitation.Code): Boolean {
        val campaign = campaigns[campaignId] ?: return false
        if (!invitations.markUsed(redeemed)) return false
        campaigns[campaignId] = campaign.copy(players = campaign.players + player)
        return true
    }

    private fun Campaign.overview() = CampaignOverview(id, name, image, gameMaster, players)
}
