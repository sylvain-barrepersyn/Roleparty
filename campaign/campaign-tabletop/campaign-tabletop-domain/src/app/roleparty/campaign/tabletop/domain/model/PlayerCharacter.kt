package app.roleparty.campaign.tabletop.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId

data class PlayerCharacter(
    val id: PlayerCharacterId,
    val campaignId: CampaignId,
    val player: AccountId,
    val name: String,
    val imageId: ImageId,
)
