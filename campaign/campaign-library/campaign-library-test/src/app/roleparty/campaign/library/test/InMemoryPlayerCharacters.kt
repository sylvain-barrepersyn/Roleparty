package app.roleparty.campaign.library.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.domain.port.secondary.PlayerCharacters

internal class InMemoryPlayerCharacters : PlayerCharacters {

    private val players = mutableMapOf<PlayerCharacterId, AccountId>()

    override suspend fun playerOf(playerCharacterId: PlayerCharacterId) = players[playerCharacterId]

    fun given(playerCharacterId: PlayerCharacterId, player: AccountId) {
        players[playerCharacterId] = player
    }
}
