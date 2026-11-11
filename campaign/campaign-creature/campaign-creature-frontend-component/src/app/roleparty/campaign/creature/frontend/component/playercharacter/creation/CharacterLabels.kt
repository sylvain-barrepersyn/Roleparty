package app.roleparty.campaign.creature.frontend.component.playercharacter.creation

import androidx.compose.runtime.Composable
import app.roleparty.campaign.creature.contract.web.AbilityName
import app.roleparty.campaign.creature.contract.web.AlignmentName
import app.roleparty.campaign.creature.contract.web.ArmorTrainingName
import app.roleparty.campaign.creature.contract.web.HitDieName
import app.roleparty.campaign.creature.contract.web.SizeName
import app.roleparty.campaign.creature.contract.web.SkillName
import app.roleparty.campaign.creature.frontend.component.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AbilityName.label(): String = stringResource(
    when (this) {
        AbilityName.STRENGTH -> Res.string.ability_strength
        AbilityName.DEXTERITY -> Res.string.ability_dexterity
        AbilityName.CONSTITUTION -> Res.string.ability_constitution
        AbilityName.INTELLIGENCE -> Res.string.ability_intelligence
        AbilityName.WISDOM -> Res.string.ability_wisdom
        AbilityName.CHARISMA -> Res.string.ability_charisma
    }
)

@Composable
internal fun SkillName.label(): String = stringResource(
    when (this) {
        SkillName.ACROBATICS -> Res.string.skill_acrobatics
        SkillName.ANIMAL_HANDLING -> Res.string.skill_animal_handling
        SkillName.ARCANA -> Res.string.skill_arcana
        SkillName.ATHLETICS -> Res.string.skill_athletics
        SkillName.DECEPTION -> Res.string.skill_deception
        SkillName.HISTORY -> Res.string.skill_history
        SkillName.INSIGHT -> Res.string.skill_insight
        SkillName.INTIMIDATION -> Res.string.skill_intimidation
        SkillName.INVESTIGATION -> Res.string.skill_investigation
        SkillName.MEDICINE -> Res.string.skill_medicine
        SkillName.NATURE -> Res.string.skill_nature
        SkillName.PERCEPTION -> Res.string.skill_perception
        SkillName.PERFORMANCE -> Res.string.skill_performance
        SkillName.PERSUASION -> Res.string.skill_persuasion
        SkillName.RELIGION -> Res.string.skill_religion
        SkillName.SLEIGHT_OF_HAND -> Res.string.skill_sleight_of_hand
        SkillName.STEALTH -> Res.string.skill_stealth
        SkillName.SURVIVAL -> Res.string.skill_survival
    }
)

@Composable
internal fun AlignmentName.label(): String = stringResource(
    when (this) {
        AlignmentName.LAWFUL_GOOD -> Res.string.alignment_lawful_good
        AlignmentName.NEUTRAL_GOOD -> Res.string.alignment_neutral_good
        AlignmentName.CHAOTIC_GOOD -> Res.string.alignment_chaotic_good
        AlignmentName.LAWFUL_NEUTRAL -> Res.string.alignment_lawful_neutral
        AlignmentName.NEUTRAL -> Res.string.alignment_neutral
        AlignmentName.CHAOTIC_NEUTRAL -> Res.string.alignment_chaotic_neutral
        AlignmentName.LAWFUL_EVIL -> Res.string.alignment_lawful_evil
        AlignmentName.NEUTRAL_EVIL -> Res.string.alignment_neutral_evil
        AlignmentName.CHAOTIC_EVIL -> Res.string.alignment_chaotic_evil
    }
)

@Composable
internal fun SizeName.label(): String = stringResource(
    when (this) {
        SizeName.TINY -> Res.string.size_tiny
        SizeName.SMALL -> Res.string.size_small
        SizeName.MEDIUM -> Res.string.size_medium
        SizeName.LARGE -> Res.string.size_large
    }
)

@Composable
internal fun ArmorTrainingName.label(): String = stringResource(
    when (this) {
        ArmorTrainingName.LIGHT -> Res.string.armor_light
        ArmorTrainingName.MEDIUM -> Res.string.armor_medium
        ArmorTrainingName.HEAVY -> Res.string.armor_heavy
        ArmorTrainingName.SHIELDS -> Res.string.armor_shields
    }
)

internal fun HitDieName.label(): String = "d$faces"

@Composable
internal fun AbilityScoreMethod.label(): String = stringResource(
    when (this) {
        AbilityScoreMethod.STANDARD_ARRAY -> Res.string.method_standard_array
        AbilityScoreMethod.RANDOM -> Res.string.method_random
        AbilityScoreMethod.POINT_BUY -> Res.string.method_point_buy
    }
)

@Composable
internal fun BackgroundBonus.label(): String = stringResource(
    when (this) {
        BackgroundBonus.PLUS_TWO_PLUS_ONE -> Res.string.bonus_two_one
        BackgroundBonus.PLUS_ONE_TO_THREE -> Res.string.bonus_one_one_one
    }
)

@Composable
internal fun PlayerCharacterCreationStep.label(): String = stringResource(
    when (this) {
        PlayerCharacterCreationStep.CLASS -> Res.string.step_class
        PlayerCharacterCreationStep.ORIGIN -> Res.string.step_origin
        PlayerCharacterCreationStep.ABILITY_SCORES -> Res.string.step_ability_scores
        PlayerCharacterCreationStep.ALIGNMENT -> Res.string.step_alignment
        PlayerCharacterCreationStep.DETAILS -> Res.string.step_details
        PlayerCharacterCreationStep.NAME -> Res.string.step_name
    }
)

