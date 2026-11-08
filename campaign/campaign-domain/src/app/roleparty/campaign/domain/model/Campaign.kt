package app.roleparty.campaign.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.media.commons.ImageId

data class Campaign(
    val id: CampaignId,
    val name: String,
    val description: String,
    val image: ImageId,
    val gameMaster: AccountId,
    val players: Set<AccountId> = emptySet(),
) {
    operator fun contains(accountId: AccountId) = isGameMaster(accountId) || accountId in players

    fun isGameMaster(accountId: AccountId) = accountId == gameMaster
}
