package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacterOverview
import app.roleparty.campaign.domain.port.primary.FindPlayerCharacters
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.domain.port.secondary.PlayerCharacters
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.getOrElse

internal class PlayerCharacterFindingService(
    private val playerCharacters: PlayerCharacters,
    private val campaigns: Campaigns,
    private val access: CampaignAccessCheckingService,
) : FindPlayerCharacters {

    override suspend fun ofRequester(requester: AccountId, campaignId: CampaignId): Outcome<List<PlayerCharacterOverview>, CampaignError> {
        access.asParticipant(requester, campaignId).getOrElse { return Outcome.Failure(it) }

        return Outcome.Success(playerCharacters.ofPlayer(campaignId, requester).map { it.overview() })
    }

    override suspend fun byId(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<PlayerCharacter, CampaignError> {
        val playerCharacter = playerCharacters.findById(playerCharacterId)
            ?: return Outcome.Failure(CampaignError.PlayerCharacterNotFound(playerCharacterId))
        if (playerCharacter.player == requester) return Outcome.Success(playerCharacter)

        val campaign = campaigns.findById(playerCharacter.campaignId)
        if (campaign?.isGameMaster(requester) == true) return Outcome.Success(playerCharacter)
        return Outcome.Failure(CampaignError.PlayerCharacterOwnerRequired)
    }

    private fun PlayerCharacter.overview() = PlayerCharacterOverview(id, name, image, species, level)
}
