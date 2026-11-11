package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.port.primary.UnlinkCharacterSheet
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets
import app.roleparty.core.commons.Outcome

internal class CharacterSheetUnlinkingService(
    private val characterSheets: CharacterSheets,
    private val resolver: CharacterStatsResolvingService,
    private val gameMaster: GameMasterCheckingService,
) : UnlinkCharacterSheet {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, characterSheetId: CharacterSheetId): Outcome<Unit, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }
        val character = characterSheets.findById(characterSheetId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(CreatureError.CharacterSheetNotFound(characterSheetId))
        if (character.stats !is CharacterStats.Inherited) return Outcome.Failure(CreatureError.CharacterSheetNotLinked)

        val resolved = when (val outcome = resolver.resolve(character)) {
            is Outcome.Success -> outcome.value
            is Outcome.Failure -> return outcome
        }
        characterSheets.save(character.copy(stats = CharacterStats.Own(resolved.stats)))
        return Outcome.success()
    }
}
