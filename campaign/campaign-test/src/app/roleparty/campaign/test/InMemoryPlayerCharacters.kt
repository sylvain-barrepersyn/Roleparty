package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.port.secondary.PlayerCharacters

internal class InMemoryPlayerCharacters : PlayerCharacters {

    private val playerCharacters = linkedMapOf<PlayerCharacterId, PlayerCharacter>()

    override suspend fun save(playerCharacter: PlayerCharacter) {
        playerCharacters[playerCharacter.id] = playerCharacter
    }

    override suspend fun findById(playerCharacterId: PlayerCharacterId) = playerCharacters[playerCharacterId]

    override suspend fun ofPlayer(campaignId: CampaignId, player: AccountId) =
        playerCharacters.values.filter { it.campaignId == campaignId && it.player == player }
}
