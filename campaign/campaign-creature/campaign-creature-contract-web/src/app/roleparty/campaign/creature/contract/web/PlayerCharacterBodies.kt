package app.roleparty.campaign.creature.contract.web

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId
import kotlinx.serialization.Serializable

@Serializable
enum class AbilityName { STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA }

@Serializable
enum class SkillName(val ability: AbilityName) {
    ACROBATICS(AbilityName.DEXTERITY),
    ANIMAL_HANDLING(AbilityName.WISDOM),
    ARCANA(AbilityName.INTELLIGENCE),
    ATHLETICS(AbilityName.STRENGTH),
    DECEPTION(AbilityName.CHARISMA),
    HISTORY(AbilityName.INTELLIGENCE),
    INSIGHT(AbilityName.WISDOM),
    INTIMIDATION(AbilityName.CHARISMA),
    INVESTIGATION(AbilityName.INTELLIGENCE),
    MEDICINE(AbilityName.WISDOM),
    NATURE(AbilityName.INTELLIGENCE),
    PERCEPTION(AbilityName.WISDOM),
    PERFORMANCE(AbilityName.CHARISMA),
    PERSUASION(AbilityName.CHARISMA),
    RELIGION(AbilityName.INTELLIGENCE),
    SLEIGHT_OF_HAND(AbilityName.DEXTERITY),
    STEALTH(AbilityName.DEXTERITY),
    SURVIVAL(AbilityName.WISDOM),
}

@Serializable
enum class AlignmentName { LAWFUL_GOOD, NEUTRAL_GOOD, CHAOTIC_GOOD, LAWFUL_NEUTRAL, NEUTRAL, CHAOTIC_NEUTRAL, LAWFUL_EVIL, NEUTRAL_EVIL, CHAOTIC_EVIL }

@Serializable
enum class SizeName { TINY, SMALL, MEDIUM, LARGE }

@Serializable
enum class ArmorTrainingName { LIGHT, MEDIUM, HEAVY, SHIELDS }

@Serializable
enum class HitDieName(val faces: Int) { D4(4), D6(6), D8(8), D10(10), D12(12) }

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
data class BackgroundBody(val name: String, val originFeat: String, val toolProficiency: String)

@Serializable
data class SpeciesBody(val name: String, val size: SizeName, val speed: Int, val traits: String)

@Serializable
data class PlayerCharacterDetailsBody(val appearance: String, val personality: String, val backstory: String, val other: String)

@Serializable
data class ClassLevelBody(val name: String, val level: Int, val hitDie: HitDieName)

@Serializable
data class PlayerCharacterCreationRequestBody(
    val name: String,
    val imageId: ImageId,
    val className: String,
    val hitDie: HitDieName,
    val classFeatures: String,
    val savingThrows: Set<AbilityName>,
    val armorTrainings: Set<ArmorTrainingName>,
    val background: BackgroundBody,
    val skills: Set<SkillName>,
    val species: SpeciesBody,
    val languages: List<String>,
    val equipment: String,
    val coins: Int,
    val abilityScores: AbilityScoresBody,
    val alignment: AlignmentName,
    val details: PlayerCharacterDetailsBody,
)

@Serializable
data class PlayerCharacterCreatedResponseBody(val id: PlayerCharacterId)

@Serializable
data class PlayerCharacterOverviewResponseBody(
    val id: PlayerCharacterId,
    val name: String,
    val imageId: ImageId,
    val classes: List<ClassLevelBody>,
    val species: String,
    val level: Int,
)

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
    val maxHitPoints: Int,
    val armorClass: Int,
    val initiative: Int,
    val passivePerception: Int,
    val classFeatures: String,
    val savingThrows: Set<AbilityName>,
    val armorTrainings: Set<ArmorTrainingName>,
    val background: BackgroundBody,
    val skills: Set<SkillName>,
    val species: SpeciesBody,
    val languages: List<String>,
    val equipment: String,
    val coins: Int,
    val abilityScores: AbilityScoresBody,
    val alignment: AlignmentName,
    val details: PlayerCharacterDetailsBody,
)
