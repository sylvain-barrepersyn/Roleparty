package app.roleparty.campaign.creature.domain.service

import app.roleparty.campaign.creature.domain.model.CreatureError.InvalidSheet.Field
import app.roleparty.campaign.creature.domain.model.Defenses
import app.roleparty.campaign.creature.domain.model.LegendaryActions
import app.roleparty.campaign.creature.domain.model.MovementMode
import app.roleparty.campaign.creature.domain.model.StatBlock

internal class StatBlockValidatingService {

    fun normalized(stats: StatBlock) = with(stats) {
        copy(
            skills = skills.filterValues { it > 0 },
            taxonomy = taxonomy.copy(tags = taxonomy.tags.trim()),
            languages = languages.copy(understood = languages.understood - languages.known, other = languages.other.trim()),
            gear = gear.map { it.trim() },
        )
    }

    fun invalidFieldOf(stats: StatBlock): Field? = when {
        stats.taxonomy.alternativeSize == stats.size || stats.taxonomy.swarmOf?.let { it >= stats.size } == true -> Field.TAXONOMY
        stats.armorClass.value <= 0 -> Field.ARMOR_CLASS
        stats.hitPoints.max <= 0 || stats.hitPoints.hitDice.count <= 0 || stats.hitPoints.hitDice.sides < 2 -> Field.HIT_POINTS
        stats.speeds.isEmpty() || stats.speeds.values.any { it < 0 } || (stats.hover && (stats.speeds[MovementMode.FLY] ?: 0) <= 0) -> Field.SPEEDS
        stats.abilities.all.any { it !in ABILITY_RANGE } -> Field.ABILITIES
        stats.savingThrows.all.any { it !in SAVING_THROW_RANGE } -> Field.SAVING_THROWS
        stats.skills.values.any { it !in SKILL_RANGE } -> Field.SKILLS
        stats.senses.values.any { it <= 0 } -> Field.SENSES
        stats.passivePerception < 0 -> Field.PASSIVE_PERCEPTION
        stats.defenses.overlapping() -> Field.DEFENSES
        (stats.languages.telepathyFeet ?: 0) < 0 -> Field.LANGUAGES
        stats.gear.any { it.isBlank() } -> Field.GEAR
        (stats.traits + stats.actions + stats.bonusActions + stats.reactions).any { it.name.isBlank() } -> Field.FEATURES
        stats.legendaryActions.invalid() -> Field.LEGENDARY_ACTIONS
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
