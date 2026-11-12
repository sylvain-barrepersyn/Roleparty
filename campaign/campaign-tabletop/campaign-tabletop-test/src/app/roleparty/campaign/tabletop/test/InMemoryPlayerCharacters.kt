package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.model.PlayerCharacter
import app.roleparty.campaign.tabletop.domain.port.secondary.PlayerCharacters

internal class InMemoryPlayerCharacters : PlayerCharacters {

    private val playerCharacters = mutableMapOf<PlayerCharacterId, PlayerCharacter>()

    override suspend fun findById(playerCharacterId: PlayerCharacterId) = playerCharacters[playerCharacterId]

    fun given(playerCharacter: PlayerCharacter) {
        playerCharacters[playerCharacter.id] = playerCharacter
    }
}
