package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterOverview
import app.roleparty.campaign.creature.domain.port.primary.FindPlayerCharacters
import app.roleparty.campaign.creature.domain.port.secondary.Campaigns
import app.roleparty.campaign.creature.domain.port.secondary.PlayerCharacters
import app.roleparty.core.commons.Outcome

internal class PlayerCharacterFindingService(
    private val playerCharacters: PlayerCharacters,
    private val campaigns: Campaigns,
    private val participant: ParticipantCheckingService,
) : FindPlayerCharacters {

    override suspend fun ofRequester(requester: AccountId, campaignId: CampaignId): Outcome<List<PlayerCharacterOverview>, CreatureError> {
        participant.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        return Outcome.Success(playerCharacters.ofPlayer(campaignId, requester).map { it.overview() })
    }

    override suspend fun byId(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<PlayerCharacter, CreatureError> {
        val playerCharacter = playerCharacters.findById(playerCharacterId)
            ?: return Outcome.Failure(CreatureError.PlayerCharacterNotFound(playerCharacterId))
        if (playerCharacter.player == requester) return Outcome.Success(playerCharacter)

        if (campaigns.gameMasterOf(playerCharacter.campaignId) == requester) return Outcome.Success(playerCharacter)
        return Outcome.Failure(CreatureError.PlayerCharacterOwnerRequired)
    }

    private fun PlayerCharacter.overview() = PlayerCharacterOverview(id, name, image, classes, species.name, level)
}
