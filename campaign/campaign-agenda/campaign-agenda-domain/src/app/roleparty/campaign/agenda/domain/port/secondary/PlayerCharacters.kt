package app.roleparty.campaign.agenda.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId

interface PlayerCharacters {

    suspend fun namesByPlayer(campaignId: CampaignId): Map<AccountId, List<String>>
}
