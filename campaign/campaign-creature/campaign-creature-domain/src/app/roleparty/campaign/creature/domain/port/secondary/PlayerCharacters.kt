package app.roleparty.campaign.creature.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacter

interface PlayerCharacters {

    suspend fun save(playerCharacter: PlayerCharacter)

    suspend fun findById(playerCharacterId: PlayerCharacterId): PlayerCharacter?

    suspend fun ofPlayer(campaignId: CampaignId, player: AccountId): List<PlayerCharacter>
}
