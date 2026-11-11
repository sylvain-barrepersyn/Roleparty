package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheetChange
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.CreatureError.InvalidSheet.Field
import app.roleparty.campaign.creature.domain.port.primary.UpdateCharacterSheet
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets
import app.roleparty.core.commons.Outcome

internal class CharacterSheetUpdatingService(
    private val characterSheets: CharacterSheets,
    private val gameMaster: GameMasterCheckingService,
    private val statBlocks: StatBlockValidatingService,
) : UpdateCharacterSheet {

    override suspend fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        characterSheetId: CharacterSheetId,
        change: CharacterSheetChange,
    ): Outcome<Unit, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }
        val character = characterSheets.findById(characterSheetId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(CreatureError.CharacterSheetNotFound(characterSheetId))
        if (change.name.isBlank()) return Outcome.Failure(CreatureError.InvalidSheet(Field.NAME))

        val stats = when (character.stats) {
            is CharacterStats.Inherited -> if (change.stats == null) character.stats else return Outcome.Failure(CreatureError.InheritedStatsReadOnly)
            is CharacterStats.Own -> change.stats?.let { stats ->
                statBlocks.invalidFieldOf(stats)?.let { return Outcome.Failure(CreatureError.InvalidSheet(it)) }
                CharacterStats.Own(statBlocks.normalized(stats))
            } ?: character.stats
        }
        characterSheets.save(character.copy(name = change.name.trim(), image = change.image, notes = change.notes.trim(), stats = stats))
        return Outcome.success()
    }
}
