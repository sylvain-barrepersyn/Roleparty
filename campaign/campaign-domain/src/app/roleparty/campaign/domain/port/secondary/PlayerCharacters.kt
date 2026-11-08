package app.roleparty.campaign.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter

interface PlayerCharacters {

    suspend fun save(playerCharacter: PlayerCharacter)

    suspend fun findById(playerCharacterId: PlayerCharacterId): PlayerCharacter?

    suspend fun ofPlayer(campaignId: CampaignId, player: AccountId): List<PlayerCharacter>
}
