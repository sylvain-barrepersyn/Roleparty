package app.roleparty.campaign.creature.frontend.component

import androidx.compose.runtime.Composable
import app.roleparty.campaign.creature.contract.web.ChallengeRatingName
import app.roleparty.campaign.creature.contract.web.CreatureLanguageName
import app.roleparty.campaign.creature.contract.web.CreatureSizeName
import app.roleparty.campaign.creature.contract.web.CreatureSkillName
import app.roleparty.campaign.creature.contract.web.CreatureTypeName
import app.roleparty.campaign.creature.contract.web.MovementModeName
import app.roleparty.campaign.creature.contract.web.CreatureAlignmentName
import app.roleparty.campaign.creature.contract.web.InitiativeProficiencyName
import app.roleparty.campaign.creature.contract.web.SenseName
import app.roleparty.campaign.creature.contract.web.DamageTypeName
import app.roleparty.campaign.creature.contract.web.ConditionName
import app.roleparty.campaign.creature.contract.web.HabitatName
import app.roleparty.campaign.creature.contract.web.TreasureName
import app.roleparty.campaign.creature.frontend.component.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreatureSizeName.label() = stringResource(
    when (this) {
        CreatureSizeName.TINY -> Res.string.size_TINY
        CreatureSizeName.SMALL -> Res.string.size_SMALL
        CreatureSizeName.MEDIUM -> Res.string.size_MEDIUM
        CreatureSizeName.LARGE -> Res.string.size_LARGE
        CreatureSizeName.HUGE -> Res.string.size_HUGE
        CreatureSizeName.GARGANTUAN -> Res.string.size_GARGANTUAN
    }
)

@Composable
fun CreatureSizeName.label(type: CreatureTypeName) = if (!type.masculine) label() else stringResource(
    when (this) {
        CreatureSizeName.TINY -> Res.string.size_TINY_m
        CreatureSizeName.SMALL -> Res.string.size_SMALL_m
        CreatureSizeName.MEDIUM -> Res.string.size_MEDIUM_m
        CreatureSizeName.LARGE -> Res.string.size_LARGE_m
        CreatureSizeName.HUGE -> Res.string.size_HUGE_m
        CreatureSizeName.GARGANTUAN -> Res.string.size_GARGANTUAN_m
    }
)

private val CreatureTypeName.masculine
    get() = when (this) {
        CreatureTypeName.DRAGON, CreatureTypeName.ELEMENTAL, CreatureTypeName.FIEND, CreatureTypeName.GIANT, CreatureTypeName.HUMANOID, CreatureTypeName.UNDEAD, CreatureTypeName.CELESTIAL -> true
        CreatureTypeName.ABERRATION, CreatureTypeName.BEAST, CreatureTypeName.CONSTRUCT, CreatureTypeName.FEY, CreatureTypeName.MONSTROSITY, CreatureTypeName.OOZE, CreatureTypeName.PLANT -> false
    }

@Composable
fun CreatureTypeName.label() = stringResource(
    when (this) {
        CreatureTypeName.ABERRATION -> Res.string.type_ABERRATION
        CreatureTypeName.BEAST -> Res.string.type_BEAST
        CreatureTypeName.CELESTIAL -> Res.string.type_CELESTIAL
        CreatureTypeName.CONSTRUCT -> Res.string.type_CONSTRUCT
        CreatureTypeName.DRAGON -> Res.string.type_DRAGON
        CreatureTypeName.ELEMENTAL -> Res.string.type_ELEMENTAL
        CreatureTypeName.FEY -> Res.string.type_FEY
        CreatureTypeName.FIEND -> Res.string.type_FIEND
        CreatureTypeName.GIANT -> Res.string.type_GIANT
        CreatureTypeName.HUMANOID -> Res.string.type_HUMANOID
        CreatureTypeName.MONSTROSITY -> Res.string.type_MONSTROSITY
        CreatureTypeName.OOZE -> Res.string.type_OOZE
        CreatureTypeName.PLANT -> Res.string.type_PLANT
        CreatureTypeName.UNDEAD -> Res.string.type_UNDEAD
    }
)

@Composable
fun MovementModeName.label() = stringResource(
    when (this) {
        MovementModeName.WALK -> Res.string.movement_WALK
        MovementModeName.SWIM -> Res.string.movement_SWIM
        MovementModeName.FLY -> Res.string.movement_FLY
        MovementModeName.CLIMB -> Res.string.movement_CLIMB
        MovementModeName.BURROW -> Res.string.movement_BURROW
    }
)

@Composable
fun CreatureLanguageName.label() = stringResource(
    when (this) {
        CreatureLanguageName.COMMON -> Res.string.language_COMMON
        CreatureLanguageName.COMMON_SIGN_LANGUAGE -> Res.string.language_COMMON_SIGN_LANGUAGE
        CreatureLanguageName.DRACONIC -> Res.string.language_DRACONIC
        CreatureLanguageName.DWARVISH -> Res.string.language_DWARVISH
        CreatureLanguageName.ELVISH -> Res.string.language_ELVISH
        CreatureLanguageName.GIANT -> Res.string.language_GIANT
        CreatureLanguageName.GNOMISH -> Res.string.language_GNOMISH
        CreatureLanguageName.GOBLIN -> Res.string.language_GOBLIN
        CreatureLanguageName.HALFLING -> Res.string.language_HALFLING
        CreatureLanguageName.ORC -> Res.string.language_ORC
        CreatureLanguageName.ABYSSAL -> Res.string.language_ABYSSAL
        CreatureLanguageName.CELESTIAL -> Res.string.language_CELESTIAL
        CreatureLanguageName.DEEP_SPEECH -> Res.string.language_DEEP_SPEECH
        CreatureLanguageName.DRUIDIC -> Res.string.language_DRUIDIC
        CreatureLanguageName.INFERNAL -> Res.string.language_INFERNAL
        CreatureLanguageName.PRIMORDIAL -> Res.string.language_PRIMORDIAL
        CreatureLanguageName.SYLVAN -> Res.string.language_SYLVAN
        CreatureLanguageName.THIEVES_CANT -> Res.string.language_THIEVES_CANT
        CreatureLanguageName.UNDERCOMMON -> Res.string.language_UNDERCOMMON
    }
)

@Composable
fun CreatureSkillName.label() = stringResource(
    when (this) {
        CreatureSkillName.ATHLETICS -> Res.string.skill_athletics
        CreatureSkillName.ACROBATICS -> Res.string.skill_acrobatics
        CreatureSkillName.SLEIGHT_OF_HAND -> Res.string.skill_sleight_of_hand
        CreatureSkillName.STEALTH -> Res.string.skill_stealth
        CreatureSkillName.ARCANA -> Res.string.skill_arcana
        CreatureSkillName.HISTORY -> Res.string.skill_history
        CreatureSkillName.INVESTIGATION -> Res.string.skill_investigation
        CreatureSkillName.NATURE -> Res.string.skill_nature
        CreatureSkillName.RELIGION -> Res.string.skill_religion
        CreatureSkillName.ANIMAL_HANDLING -> Res.string.skill_animal_handling
        CreatureSkillName.INSIGHT -> Res.string.skill_insight
        CreatureSkillName.MEDICINE -> Res.string.skill_medicine
        CreatureSkillName.PERCEPTION -> Res.string.skill_perception
        CreatureSkillName.SURVIVAL -> Res.string.skill_survival
        CreatureSkillName.DECEPTION -> Res.string.skill_deception
        CreatureSkillName.INTIMIDATION -> Res.string.skill_intimidation
        CreatureSkillName.PERFORMANCE -> Res.string.skill_performance
        CreatureSkillName.PERSUASION -> Res.string.skill_persuasion
    }
)

internal val CreatureSkillName.ability
    get() = when (this) {
        CreatureSkillName.ATHLETICS -> Ability.STRENGTH
        CreatureSkillName.ACROBATICS -> Ability.DEXTERITY
        CreatureSkillName.SLEIGHT_OF_HAND -> Ability.DEXTERITY
        CreatureSkillName.STEALTH -> Ability.DEXTERITY
        CreatureSkillName.ARCANA -> Ability.INTELLIGENCE
        CreatureSkillName.HISTORY -> Ability.INTELLIGENCE
        CreatureSkillName.INVESTIGATION -> Ability.INTELLIGENCE
        CreatureSkillName.NATURE -> Ability.INTELLIGENCE
        CreatureSkillName.RELIGION -> Ability.INTELLIGENCE
        CreatureSkillName.ANIMAL_HANDLING -> Ability.WISDOM
        CreatureSkillName.INSIGHT -> Ability.WISDOM
        CreatureSkillName.MEDICINE -> Ability.WISDOM
        CreatureSkillName.PERCEPTION -> Ability.WISDOM
        CreatureSkillName.SURVIVAL -> Ability.WISDOM
        CreatureSkillName.DECEPTION -> Ability.CHARISMA
        CreatureSkillName.INTIMIDATION -> Ability.CHARISMA
        CreatureSkillName.PERFORMANCE -> Ability.CHARISMA
        CreatureSkillName.PERSUASION -> Ability.CHARISMA
    }

@Composable
fun CreatureAlignmentName.label() = stringResource(
    when (this) {
        CreatureAlignmentName.LAWFUL_GOOD -> Res.string.alignment_LAWFUL_GOOD
        CreatureAlignmentName.NEUTRAL_GOOD -> Res.string.alignment_NEUTRAL_GOOD
        CreatureAlignmentName.CHAOTIC_GOOD -> Res.string.alignment_CHAOTIC_GOOD
        CreatureAlignmentName.LAWFUL_NEUTRAL -> Res.string.alignment_LAWFUL_NEUTRAL
        CreatureAlignmentName.NEUTRAL -> Res.string.alignment_NEUTRAL
        CreatureAlignmentName.CHAOTIC_NEUTRAL -> Res.string.alignment_CHAOTIC_NEUTRAL
        CreatureAlignmentName.LAWFUL_EVIL -> Res.string.alignment_LAWFUL_EVIL
        CreatureAlignmentName.NEUTRAL_EVIL -> Res.string.alignment_NEUTRAL_EVIL
        CreatureAlignmentName.CHAOTIC_EVIL -> Res.string.alignment_CHAOTIC_EVIL
        CreatureAlignmentName.UNALIGNED -> Res.string.alignment_UNALIGNED
        CreatureAlignmentName.ANY -> Res.string.alignment_ANY
    }
)

@Composable
fun InitiativeProficiencyName.label() = stringResource(
    when (this) {
        InitiativeProficiencyName.NONE -> Res.string.initiative_NONE
        InitiativeProficiencyName.PROFICIENT -> Res.string.initiative_PROFICIENT
        InitiativeProficiencyName.EXPERTISE -> Res.string.initiative_EXPERTISE
    }
)

@Composable
fun SenseName.label() = stringResource(
    when (this) {
        SenseName.BLINDSIGHT -> Res.string.sense_BLINDSIGHT
        SenseName.DARKVISION -> Res.string.sense_DARKVISION
        SenseName.TREMORSENSE -> Res.string.sense_TREMORSENSE
        SenseName.TRUESIGHT -> Res.string.sense_TRUESIGHT
    }
)

@Composable
fun DamageTypeName.label() = stringResource(
    when (this) {
        DamageTypeName.ACID -> Res.string.damage_ACID
        DamageTypeName.BLUDGEONING -> Res.string.damage_BLUDGEONING
        DamageTypeName.COLD -> Res.string.damage_COLD
        DamageTypeName.FIRE -> Res.string.damage_FIRE
        DamageTypeName.FORCE -> Res.string.damage_FORCE
        DamageTypeName.LIGHTNING -> Res.string.damage_LIGHTNING
        DamageTypeName.NECROTIC -> Res.string.damage_NECROTIC
        DamageTypeName.PIERCING -> Res.string.damage_PIERCING
        DamageTypeName.POISON -> Res.string.damage_POISON
        DamageTypeName.PSYCHIC -> Res.string.damage_PSYCHIC
        DamageTypeName.RADIANT -> Res.string.damage_RADIANT
        DamageTypeName.SLASHING -> Res.string.damage_SLASHING
        DamageTypeName.THUNDER -> Res.string.damage_THUNDER
    }
)

@Composable
fun ConditionName.label() = stringResource(
    when (this) {
        ConditionName.BLINDED -> Res.string.condition_BLINDED
        ConditionName.CHARMED -> Res.string.condition_CHARMED
        ConditionName.DEAFENED -> Res.string.condition_DEAFENED
        ConditionName.EXHAUSTION -> Res.string.condition_EXHAUSTION
        ConditionName.FRIGHTENED -> Res.string.condition_FRIGHTENED
        ConditionName.GRAPPLED -> Res.string.condition_GRAPPLED
        ConditionName.INCAPACITATED -> Res.string.condition_INCAPACITATED
        ConditionName.INVISIBLE -> Res.string.condition_INVISIBLE
        ConditionName.PARALYZED -> Res.string.condition_PARALYZED
        ConditionName.PETRIFIED -> Res.string.condition_PETRIFIED
        ConditionName.POISONED -> Res.string.condition_POISONED
        ConditionName.PRONE -> Res.string.condition_PRONE
        ConditionName.RESTRAINED -> Res.string.condition_RESTRAINED
        ConditionName.STUNNED -> Res.string.condition_STUNNED
        ConditionName.UNCONSCIOUS -> Res.string.condition_UNCONSCIOUS
    }
)

@Composable
fun HabitatName.label() = stringResource(
    when (this) {
        HabitatName.ANY -> Res.string.habitat_ANY
        HabitatName.ARCTIC -> Res.string.habitat_ARCTIC
        HabitatName.COASTAL -> Res.string.habitat_COASTAL
        HabitatName.DESERT -> Res.string.habitat_DESERT
        HabitatName.FOREST -> Res.string.habitat_FOREST
        HabitatName.GRASSLAND -> Res.string.habitat_GRASSLAND
        HabitatName.HILL -> Res.string.habitat_HILL
        HabitatName.MOUNTAIN -> Res.string.habitat_MOUNTAIN
        HabitatName.PLANAR -> Res.string.habitat_PLANAR
        HabitatName.SWAMP -> Res.string.habitat_SWAMP
        HabitatName.UNDERDARK -> Res.string.habitat_UNDERDARK
        HabitatName.UNDERWATER -> Res.string.habitat_UNDERWATER
        HabitatName.URBAN -> Res.string.habitat_URBAN
    }
)

@Composable
fun TreasureName.label() = stringResource(
    when (this) {
        TreasureName.ANY -> Res.string.treasure_ANY
        TreasureName.ARCANA -> Res.string.treasure_ARCANA
        TreasureName.ARMAMENTS -> Res.string.treasure_ARMAMENTS
        TreasureName.IMPLEMENTS -> Res.string.treasure_IMPLEMENTS
        TreasureName.INDIVIDUAL -> Res.string.treasure_INDIVIDUAL
        TreasureName.RELICS -> Res.string.treasure_RELICS
        TreasureName.NONE -> Res.string.treasure_NONE
    }
)

@Composable
fun CreatureTypeName.pluralLabel() = stringResource(
    when (this) {
        CreatureTypeName.ABERRATION -> Res.string.type_plural_ABERRATION
        CreatureTypeName.BEAST -> Res.string.type_plural_BEAST
        CreatureTypeName.CELESTIAL -> Res.string.type_plural_CELESTIAL
        CreatureTypeName.CONSTRUCT -> Res.string.type_plural_CONSTRUCT
        CreatureTypeName.DRAGON -> Res.string.type_plural_DRAGON
        CreatureTypeName.ELEMENTAL -> Res.string.type_plural_ELEMENTAL
        CreatureTypeName.FEY -> Res.string.type_plural_FEY
        CreatureTypeName.FIEND -> Res.string.type_plural_FIEND
        CreatureTypeName.GIANT -> Res.string.type_plural_GIANT
        CreatureTypeName.HUMANOID -> Res.string.type_plural_HUMANOID
        CreatureTypeName.MONSTROSITY -> Res.string.type_plural_MONSTROSITY
        CreatureTypeName.OOZE -> Res.string.type_plural_OOZE
        CreatureTypeName.PLANT -> Res.string.type_plural_PLANT
        CreatureTypeName.UNDEAD -> Res.string.type_plural_UNDEAD
    }
)

val ChallengeRatingName.proficiencyBonus get() = PROFICIENCY_BONUS[ordinal]

val ChallengeRatingName.lairExperience get() = EXPERIENCE.getOrElse(ordinal + 1) { EXPERIENCE.last() }

private val PROFICIENCY_BONUS = listOf(2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9)

val InitiativeProficiencyName.multiplier
    get() = when (this) {
        InitiativeProficiencyName.NONE -> 0
        InitiativeProficiencyName.PROFICIENT -> 1
        InitiativeProficiencyName.EXPERTISE -> 2
    }

fun ChallengeRatingName.label() = name.removePrefix("CR_").replace('_', '/')

val ChallengeRatingName.experience get() = EXPERIENCE[ordinal]

private val EXPERIENCE = listOf(10, 25, 50, 100, 200, 450, 700, 1100, 1800, 2300, 2900, 3900, 5000, 5900, 7200, 8400, 10000, 11500, 13000, 15000, 18000, 20000, 22000, 25000, 33000, 41000, 50000, 62000, 75000, 90000, 105000, 120000, 135000, 155000)

fun Int.abilityModifier(): Int = Math.floorDiv(this - 10, 2)

fun Int.signed(): String = if (this >= 0) "+$this" else toString()
