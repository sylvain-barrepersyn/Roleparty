package app.roleparty.campaign.agenda.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.commons.CampaignId

internal class InMemoryPlayerCharacters : PlayerCharacters {

    private val names = mutableMapOf<CampaignId, Map<AccountId, List<String>>>()

    override suspend fun namesByPlayer(campaignId: CampaignId) = names[campaignId].orEmpty()

    fun given(campaignId: CampaignId, player: AccountId, name: String) {
        val current = names[campaignId].orEmpty()
        names[campaignId] = current + (player to current[player].orEmpty() + name)
    }
}
