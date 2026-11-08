package app.roleparty.campaign.domain.model.playercharacter

import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId

data class PlayerCharacterOverview(
    val id: PlayerCharacterId,
    val name: String,
    val image: ImageId,
    val species: Species,
    val level: Int,
)
