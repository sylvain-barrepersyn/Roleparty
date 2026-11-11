package app.roleparty.campaign.library.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId

interface PlayerCharacters {

    suspend fun playerOf(playerCharacterId: PlayerCharacterId): AccountId?
}
