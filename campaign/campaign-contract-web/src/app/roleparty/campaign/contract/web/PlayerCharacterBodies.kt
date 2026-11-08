package app.roleparty.campaign.contract.web

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId
import kotlinx.serialization.Serializable

@Serializable
enum class PlayableClassName(val skillCount: Int) {
    BARBARIAN(2), BARD(3), CLERIC(2), DRUID(2), FIGHTER(2), MONK(2), PALADIN(2), RANGER(3), ROGUE(4), SORCERER(2), WARLOCK(2), WIZARD(2),
}

@Serializable
enum class SpeciesName { DRAGONBORN, DWARF, ELF, GNOME, GOLIATH, HALFLING, HUMAN, ORC, TIEFLING }

@Serializable
enum class SizeName { TINY, SMALL, MEDIUM, LARGE }

@Serializable
enum class BackgroundName { ACOLYTE, ARTISAN, CHARLATAN, CRIMINAL, ENTERTAINER, FARMER, GUARD, GUIDE, HERMIT, MERCHANT, NOBLE, SAGE, SAILOR, SCRIBE, SOLDIER, WAYFARER }

@Serializable
enum class AlignmentName { LAWFUL_GOOD, NEUTRAL_GOOD, CHAOTIC_GOOD, LAWFUL_NEUTRAL, NEUTRAL, CHAOTIC_NEUTRAL, LAWFUL_EVIL, NEUTRAL_EVIL, CHAOTIC_EVIL }

@Serializable
enum class SkillName {
    ACROBATICS, ANIMAL_HANDLING, ARCANA, ATHLETICS, DECEPTION, HISTORY, INSIGHT, INTIMIDATION, INVESTIGATION,
    MEDICINE, NATURE, PERCEPTION, PERFORMANCE, PERSUASION, RELIGION, SLEIGHT_OF_HAND, STEALTH, SURVIVAL,
}

@Serializable
enum class LanguageName {
    COMMON, COMMON_SIGN_LANGUAGE, DRACONIC, DWARVISH, ELVISH, GIANT, GNOMISH, GOBLIN, HALFLING, ORC,
    ABYSSAL, CELESTIAL, DEEP_SPEECH, DRUIDIC, INFERNAL, PRIMORDIAL, SYLVAN, THIEVES_CANT, UNDERCOMMON,
}

@Serializable
data class AbilityScoresBody(
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
)

@Serializable
data class PlayerCharacterCreationRequestBody(
    val name: String,
    val imageId: ImageId,
    val characterClass: PlayableClassName,
    val species: SpeciesName,
    val speciesSize: SizeName? = null,
    val background: BackgroundName,
    val backgroundTool: String? = null,
    val alignment: AlignmentName,
    val abilityScores: AbilityScoresBody,
    val appearance: String,
    val backstory: String,
    val skills: Set<SkillName>,
    val languages: Set<LanguageName>,
)

@Serializable
data class PlayerCharacterCreatedResponseBody(val id: PlayerCharacterId)

@Serializable
data class PlayerCharacterOverviewResponseBody(val id: PlayerCharacterId, val name: String, val imageId: ImageId, val species: SpeciesName, val level: Int)

@Serializable
data class ClassLevelBody(val characterClass: PlayableClassName, val level: Int)

@Serializable
data class PlayerCharacterResponseBody(
    val id: PlayerCharacterId,
    val campaignId: CampaignId,
    val player: AccountId,
    val name: String,
    val imageId: ImageId,
    val classes: List<ClassLevelBody>,
    val level: Int,
    val proficiencyBonus: Int,
    val species: SpeciesName,
    val size: SizeName,
    val background: BackgroundName,
    val backgroundTool: String?,
    val alignment: AlignmentName,
    val abilityScores: AbilityScoresBody,
    val appearance: String,
    val backstory: String,
    val skills: Set<SkillName>,
    val languages: Set<LanguageName>,
)

@Serializable
data class LevelUpRequestBody(val characterClass: PlayableClassName)
