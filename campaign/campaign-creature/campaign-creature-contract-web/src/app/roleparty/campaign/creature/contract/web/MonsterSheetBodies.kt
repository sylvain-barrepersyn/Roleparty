package app.roleparty.campaign.creature.contract.web

import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.media.commons.ImageId
import kotlinx.serialization.Serializable

@Serializable
enum class CreatureSizeName { TINY, SMALL, MEDIUM, LARGE, HUGE, GARGANTUAN }

@Serializable
enum class CreatureTypeName { ABERRATION, BEAST, CELESTIAL, CONSTRUCT, DRAGON, ELEMENTAL, FEY, FIEND, GIANT, HUMANOID, MONSTROSITY, OOZE, PLANT, UNDEAD }

@Serializable
enum class ChallengeRatingName {
    CR_0, CR_1_8, CR_1_4, CR_1_2, CR_1, CR_2, CR_3, CR_4, CR_5, CR_6, CR_7, CR_8, CR_9, CR_10, CR_11, CR_12, CR_13, CR_14, CR_15,
    CR_16, CR_17, CR_18, CR_19, CR_20, CR_21, CR_22, CR_23, CR_24, CR_25, CR_26, CR_27, CR_28, CR_29, CR_30,
}

@Serializable
enum class MovementModeName { WALK, SWIM, FLY, CLIMB, BURROW }

@Serializable
enum class CreatureAlignmentName {
    LAWFUL_GOOD, NEUTRAL_GOOD, CHAOTIC_GOOD, LAWFUL_NEUTRAL, NEUTRAL, CHAOTIC_NEUTRAL, LAWFUL_EVIL, NEUTRAL_EVIL, CHAOTIC_EVIL, UNALIGNED, ANY,
}

@Serializable
enum class InitiativeProficiencyName { NONE, PROFICIENT, EXPERTISE }

@Serializable
enum class SenseName { BLINDSIGHT, DARKVISION, TREMORSENSE, TRUESIGHT }

@Serializable
enum class DamageTypeName { ACID, BLUDGEONING, COLD, FIRE, FORCE, LIGHTNING, NECROTIC, PIERCING, POISON, PSYCHIC, RADIANT, SLASHING, THUNDER }

@Serializable
enum class ConditionName {
    BLINDED, CHARMED, DEAFENED, EXHAUSTION, FRIGHTENED, GRAPPLED, INCAPACITATED, INVISIBLE, PARALYZED, PETRIFIED, POISONED, PRONE, RESTRAINED, STUNNED,
    UNCONSCIOUS,
}

@Serializable
enum class HabitatName { ANY, ARCTIC, COASTAL, DESERT, FOREST, GRASSLAND, HILL, MOUNTAIN, PLANAR, SWAMP, UNDERDARK, UNDERWATER, URBAN }

@Serializable
enum class TreasureName { ANY, ARCANA, ARMAMENTS, IMPLEMENTS, INDIVIDUAL, RELICS, NONE }

@Serializable
enum class CreatureLanguageName {
    COMMON, COMMON_SIGN_LANGUAGE, DRACONIC, DWARVISH, ELVISH, GIANT, GNOMISH, GOBLIN, HALFLING, ORC,
    ABYSSAL, CELESTIAL, DEEP_SPEECH, DRUIDIC, INFERNAL, PRIMORDIAL, SYLVAN, THIEVES_CANT, UNDERCOMMON,
}

@Serializable
enum class CreatureSkillName {
    ATHLETICS, ACROBATICS, SLEIGHT_OF_HAND, STEALTH, ARCANA, HISTORY, INVESTIGATION, NATURE, RELIGION, ANIMAL_HANDLING, INSIGHT, MEDICINE, PERCEPTION, SURVIVAL, DECEPTION, INTIMIDATION, PERFORMANCE, PERSUASION,
}

@Serializable
data class MonsterSheetCreatedResponseBody(val id: MonsterSheetId)

@Serializable
data class MonsterSheetOverviewResponseBody(
    val id: MonsterSheetId,
    val name: String,
    val imageId: ImageId?,
    val type: CreatureTypeName,
    val size: CreatureSizeName,
    val challengeRating: ChallengeRatingName,
)

@Serializable
data class MonsterSheetBody(
    val name: String,
    val imageId: ImageId?,
    val size: CreatureSizeName,
    val type: CreatureTypeName,
    val challengeRating: ChallengeRatingName,
    val armorClass: ArmorClassBody,
    val hitPoints: HitPointsBody,
    val speeds: Map<MovementModeName, Int>,
    val abilities: AbilitiesBody,
    val savingThrows: SavingThrowsBody = SavingThrowsBody(),
    val skills: Map<CreatureSkillName, Int> = emptyMap(),
    val passivePerception: Int,
    val languages: LanguagesBody,
    val traits: List<FeatureBody>,
    val actions: List<FeatureBody>,
    val bonusActions: List<FeatureBody>,
    val reactions: List<FeatureBody>,
    val legendaryActions: LegendaryActionsBody,
    val taxonomy: TaxonomyBody = TaxonomyBody(),
    val alignment: CreatureAlignmentName = CreatureAlignmentName.UNALIGNED,
    val initiative: InitiativeProficiencyName = InitiativeProficiencyName.NONE,
    val hover: Boolean = false,
    val senses: Map<SenseName, Int> = emptyMap(),
    val defenses: DefensesBody = DefensesBody(),
    val gear: List<String> = emptyList(),
    val lair: Boolean = false,
    val habitats: Set<HabitatName> = emptySet(),
    val treasures: Set<TreasureName> = emptySet(),
) {
    @Serializable
    data class TaxonomyBody(val alternativeSize: CreatureSizeName? = null, val swarmOf: CreatureSizeName? = null, val tags: String = "")

    @Serializable
    data class DefensesBody(
        val vulnerabilities: Set<DamageTypeName> = emptySet(),
        val resistances: Set<DamageTypeName> = emptySet(),
        val immunities: Set<DamageTypeName> = emptySet(),
        val conditionImmunities: Set<ConditionName> = emptySet(),
    )

    @Serializable
    data class ArmorClassBody(val value: Int, val detail: String? = null)

    @Serializable
    data class HitPointsBody(val max: Int, val dice: String)

    @Serializable
    data class AbilitiesBody(val strength: Int, val dexterity: Int, val constitution: Int, val intelligence: Int, val wisdom: Int, val charisma: Int)

    @Serializable
    data class SavingThrowsBody(
        val strength: Int = 0,
        val dexterity: Int = 0,
        val constitution: Int = 0,
        val intelligence: Int = 0,
        val wisdom: Int = 0,
        val charisma: Int = 0,
    )

    @Serializable
    data class LanguagesBody(
        val known: Set<CreatureLanguageName>,
        val telepathyFeet: Int? = null,
        val understood: Set<CreatureLanguageName> = emptySet(),
        val other: String = "",
    )

    @Serializable
    data class FeatureBody(val name: String, val description: String)

    @Serializable
    data class LegendaryActionsBody(val description: String, val items: List<LegendaryActionBody>, val uses: Int = 3, val usesInLair: Int? = null)

    @Serializable
    data class LegendaryActionBody(val name: String, val description: String, val cost: Int)
}

@Serializable
data class MonsterSheetResponseBody(val id: MonsterSheetId, val sheet: MonsterSheetBody)
