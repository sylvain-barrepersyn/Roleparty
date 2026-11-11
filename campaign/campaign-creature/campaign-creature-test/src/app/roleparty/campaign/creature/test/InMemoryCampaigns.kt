package app.roleparty.campaign.creature.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.domain.port.secondary.Campaigns

internal class InMemoryCampaigns : Campaigns {

    private val gameMasters = mutableMapOf<CampaignId, AccountId>()
    private val players = mutableMapOf<CampaignId, Set<AccountId>>()

    override suspend fun gameMasterOf(campaignId: CampaignId) = gameMasters[campaignId]

    override suspend fun playersOf(campaignId: CampaignId) = players[campaignId].orEmpty()

    fun given(campaignId: CampaignId, gameMaster: AccountId, players: Set<AccountId> = emptySet()) {
        gameMasters[campaignId] = gameMaster
        this.players[campaignId] = players
    }

    fun givenPlayer(campaignId: CampaignId, player: AccountId) {
        players[campaignId] = players[campaignId].orEmpty() + player
    }
}
