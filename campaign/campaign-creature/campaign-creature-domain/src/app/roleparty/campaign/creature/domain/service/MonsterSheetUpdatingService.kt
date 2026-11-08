package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.CreatureError.InvalidMonsterSheet.Field
import app.roleparty.campaign.creature.domain.model.Defenses
import app.roleparty.campaign.creature.domain.model.LegendaryActions
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MovementMode
import app.roleparty.campaign.creature.domain.port.primary.UpdateMonsterSheet
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.core.commons.Outcome

internal class MonsterSheetUpdatingService(
    private val monsterSheets: MonsterSheets,
    private val gameMaster: GameMasterCheckingService,
) : UpdateMonsterSheet {

    override suspend fun invoke(requester: AccountId, monsterSheet: MonsterSheet): Outcome<Unit, CreatureError> {
        gameMaster.check(requester, monsterSheet.campaignId)?.let { return Outcome.Failure(it) }
        monsterSheets.findById(monsterSheet.id)?.takeIf { it.campaignId == monsterSheet.campaignId }
            ?: return Outcome.Failure(CreatureError.MonsterSheetNotFound(monsterSheet.id))
        invalidFieldOf(monsterSheet)?.let { return Outcome.Failure(CreatureError.InvalidMonsterSheet(it)) }

        monsterSheets.save(monsterSheet.normalized())
        return Outcome.success()
    }

    private fun MonsterSheet.normalized() = copy(
        name = name.trim(),
        skills = skills.filterValues { it > 0 },
        taxonomy = taxonomy.copy(tags = taxonomy.tags.trim()),
        languages = languages.copy(understood = languages.understood - languages.known, other = languages.other.trim()),
        gear = gear.map { it.trim() },
    )

    private fun invalidFieldOf(sheet: MonsterSheet): Field? = when {
        sheet.name.isBlank() -> Field.NAME
        sheet.taxonomy.alternativeSize == sheet.size || sheet.taxonomy.swarmOf?.let { it >= sheet.size } == true -> Field.TAXONOMY
        sheet.armorClass.value <= 0 -> Field.ARMOR_CLASS
        sheet.hitPoints.max <= 0 || sheet.hitPoints.hitDice.count <= 0 || sheet.hitPoints.hitDice.sides < 2 -> Field.HIT_POINTS
        sheet.speeds.isEmpty() || sheet.speeds.values.any { it < 0 } || (sheet.hover && (sheet.speeds[MovementMode.FLY] ?: 0) <= 0) -> Field.SPEEDS
        sheet.abilities.all.any { it !in ABILITY_RANGE } -> Field.ABILITIES
        sheet.savingThrows.all.any { it !in SAVING_THROW_RANGE } -> Field.SAVING_THROWS
        sheet.skills.values.any { it !in SKILL_RANGE } -> Field.SKILLS
        sheet.senses.values.any { it <= 0 } -> Field.SENSES
        sheet.passivePerception < 0 -> Field.PASSIVE_PERCEPTION
        sheet.defenses.overlapping() -> Field.DEFENSES
        (sheet.languages.telepathyFeet ?: 0) < 0 -> Field.LANGUAGES
        sheet.gear.any { it.isBlank() } -> Field.GEAR
        (sheet.traits + sheet.actions + sheet.bonusActions + sheet.reactions).any { it.name.isBlank() } -> Field.FEATURES
        sheet.legendaryActions.invalid() -> Field.LEGENDARY_ACTIONS
        else -> null
    }

    private fun Defenses.overlapping() =
        (vulnerabilities intersect resistances).isNotEmpty() || (vulnerabilities intersect immunities).isNotEmpty() || (resistances intersect immunities).isNotEmpty()

    private fun LegendaryActions.invalid() =
        items.any { it.name.isBlank() || it.cost <= 0 } || uses <= 0 || usesInLair?.let { it < uses } == true

    private companion object {
        val ABILITY_RANGE = 1..30
        val SAVING_THROW_RANGE = 0..20
        val SKILL_RANGE = 0..20
    }
}
