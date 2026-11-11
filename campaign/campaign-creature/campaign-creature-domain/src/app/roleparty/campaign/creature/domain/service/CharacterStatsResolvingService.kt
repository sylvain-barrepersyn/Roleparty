package app.roleparty.campaign.creature.domain.service

import app.roleparty.campaign.creature.domain.model.CharacterSheet
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.ResolvedCharacterSheet
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.core.commons.Outcome

internal class CharacterStatsResolvingService(private val monsterSheets: MonsterSheets) {

    suspend fun resolve(character: CharacterSheet): Outcome<ResolvedCharacterSheet, CreatureError> = when (val stats = character.stats) {
        is CharacterStats.Own -> Outcome.Success(ResolvedCharacterSheet(character, stats.stats))
        is CharacterStats.Inherited -> monsterSheets.findById(stats.monsterSheetId)
            ?.let { Outcome.Success(ResolvedCharacterSheet(character, it.stats)) }
            ?: Outcome.Failure(CreatureError.MonsterSheetNotFound(stats.monsterSheetId))
    }
}
