package app.roleparty.campaign.tabletop.domain.port.secondary

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.model.PlayerCharacter

interface PlayerCharacters {

    suspend fun findById(playerCharacterId: PlayerCharacterId): PlayerCharacter?
}
