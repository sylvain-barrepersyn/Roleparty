package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheetOverview
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.ResolvedCharacterSheet
import app.roleparty.campaign.creature.domain.port.primary.FindCharacterSheets
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets
import app.roleparty.core.commons.Outcome

internal class CharacterSheetFindingService(
    private val characterSheets: CharacterSheets,
    private val resolver: CharacterStatsResolvingService,
    private val gameMaster: GameMasterCheckingService,
) : FindCharacterSheets {

    override suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<CharacterSheetOverview>, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        val overviews = characterSheets.ofCampaign(campaignId).map { character ->
            when (val resolved = resolver.resolve(character)) {
                is Outcome.Success -> resolved.value.overview()
                is Outcome.Failure -> return resolved
            }
        }
        return Outcome.Success(overviews)
    }

    override suspend fun byId(requester: AccountId, campaignId: CampaignId, characterSheetId: CharacterSheetId): Outcome<ResolvedCharacterSheet, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        val character = characterSheets.findById(characterSheetId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(CreatureError.CharacterSheetNotFound(characterSheetId))
        return resolver.resolve(character)
    }

    private fun ResolvedCharacterSheet.overview() =
        CharacterSheetOverview(character.id, character.name, character.image, stats.type, stats.size, stats.challengeRating, linkedTo)
}
