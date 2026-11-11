package app.roleparty.campaign.creature.domain.model.playercharacter

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId

data class PlayerCharacterOverview(
    val id: PlayerCharacterId,
    val name: String,
    val image: ImageId,
    val classes: List<CharacterClass>,
    val species: String,
    val level: Int,
)
