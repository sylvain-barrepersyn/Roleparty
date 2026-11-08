package app.roleparty.campaign.creature.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.media.commons.ImageId

data class MonsterSheet(
    val id: MonsterSheetId,
    val campaignId: CampaignId,
    val name: String,
    val image: ImageId?,
    val size: CreatureSize,
    val type: CreatureType,
    val challengeRating: ChallengeRating,
    val armorClass: ArmorClass,
    val hitPoints: HitPoints,
    val speeds: Map<MovementMode, Int>,
    val abilities: AbilityScores,
    val savingThrows: SavingThrows,
    val skills: Map<CreatureSkill, Int>,
    val passivePerception: Int,
    val languages: Languages,
    val traits: List<Feature>,
    val actions: List<Feature>,
    val bonusActions: List<Feature>,
    val reactions: List<Feature>,
    val legendaryActions: LegendaryActions,
    val taxonomy: Taxonomy = Taxonomy(),
    val alignment: CreatureAlignment = CreatureAlignment.UNALIGNED,
    val initiative: InitiativeProficiency = InitiativeProficiency.NONE,
    val hover: Boolean = false,
    val senses: Map<Sense, Int> = emptyMap(),
    val defenses: Defenses = Defenses(),
    val gear: List<String> = emptyList(),
    val lair: Boolean = false,
    val habitats: Set<Habitat> = emptySet(),
    val treasures: Set<Treasure> = emptySet(),
) {
    companion object {
        fun empty(id: MonsterSheetId, campaignId: CampaignId) = MonsterSheet(
            id = id,
            campaignId = campaignId,
            name = "Monster",
            image = null,
            size = CreatureSize.MEDIUM,
            type = CreatureType.BEAST,
            challengeRating = ChallengeRating.CR_1,
            armorClass = ArmorClass(10),
            hitPoints = HitPoints(30, DiceExpression(4, 8, 5)),
            speeds = mapOf(MovementMode.WALK to 30),
            abilities = AbilityScores(10, 10, 10, 10, 10, 10),
            savingThrows = SavingThrows(),
            skills = emptyMap(),
            passivePerception = 10,
            languages = Languages(),
            traits = emptyList(),
            actions = emptyList(),
            bonusActions = emptyList(),
            reactions = emptyList(),
            legendaryActions = LegendaryActions(),
        )
    }
}

data class ArmorClass(val value: Int, val detail: String? = null)

data class HitPoints(val max: Int, val hitDice: DiceExpression)

data class DiceExpression(val count: Int, val sides: Int, val modifier: Int = 0) {

    override fun toString() = "${count}d$sides" + when {
        modifier > 0 -> "+$modifier"
        modifier < 0 -> "$modifier"
        else -> ""
    }

    companion object {
        private val PATTERN = Regex("""\s*(\d+)\s*d\s*(\d+)\s*(?:([+-])\s*(\d+))?\s*""")

        fun parse(value: String): DiceExpression? {
            val match = PATTERN.matchEntire(value) ?: return null
            val groups = match.groupValues
            val modifier = groups[4].toIntOrNull()?.let { if (groups[3] == "-") -it else it } ?: 0
            return DiceExpression(groups[1].toIntOrNull() ?: return null, groups[2].toIntOrNull() ?: return null, modifier)
        }
    }
}

data class AbilityScores(
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
) {
    val all get() = listOf(strength, dexterity, constitution, intelligence, wisdom, charisma)
}

data class SavingThrows(
    val strength: Int = 0,
    val dexterity: Int = 0,
    val constitution: Int = 0,
    val intelligence: Int = 0,
    val wisdom: Int = 0,
    val charisma: Int = 0,
) {
    val all get() = listOf(strength, dexterity, constitution, intelligence, wisdom, charisma)
}

enum class CreatureSkill {
    ATHLETICS, ACROBATICS, SLEIGHT_OF_HAND, STEALTH, ARCANA, HISTORY, INVESTIGATION, NATURE, RELIGION, ANIMAL_HANDLING, INSIGHT, MEDICINE, PERCEPTION, SURVIVAL, DECEPTION, INTIMIDATION, PERFORMANCE, PERSUASION,
}

data class Languages(
    val known: Set<CreatureLanguage> = emptySet(),
    val telepathyFeet: Int? = null,
    val understood: Set<CreatureLanguage> = emptySet(),
    val other: String = "",
)

data class Feature(val name: String, val description: String)

data class LegendaryActions(
    val description: String = "",
    val items: List<LegendaryAction> = emptyList(),
    val uses: Int = 3,
    val usesInLair: Int? = null,
)

data class Taxonomy(val alternativeSize: CreatureSize? = null, val swarmOf: CreatureSize? = null, val tags: String = "")

data class Defenses(
    val vulnerabilities: Set<DamageType> = emptySet(),
    val resistances: Set<DamageType> = emptySet(),
    val immunities: Set<DamageType> = emptySet(),
    val conditionImmunities: Set<Condition> = emptySet(),
)

data class LegendaryAction(val name: String, val description: String, val cost: Int)

data class MonsterSheetOverview(
    val id: MonsterSheetId,
    val name: String,
    val image: ImageId?,
    val type: CreatureType,
    val size: CreatureSize,
    val challengeRating: ChallengeRating,
)
