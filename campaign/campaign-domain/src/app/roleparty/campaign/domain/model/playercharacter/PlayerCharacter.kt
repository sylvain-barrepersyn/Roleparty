package app.roleparty.campaign.domain.model.playercharacter

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.domain.model.playercharacter.classes.PlayableClass
import app.roleparty.media.commons.ImageId

data class PlayerCharacter(
    val id: PlayerCharacterId,
    val campaignId: CampaignId,
    val player: AccountId,
    val name: String,
    val image: ImageId,
    val alignment: Alignment,
    val abilityScores: AbilityScores,
    val species: Species,
    val background: Background,
    val appearance: String,
    val backstory: String,
    val classes: List<PlayableClass>,
    val chosenSkillProficiencies: Set<SkillProficiency>,
    val languages: Set<Language>,
) {
    val size get() = species.size
    val level get() = classes.sumOf { it.level.toInt() }
    val armorTrainings get() = classes.flatMap { it.armorTrainings }.toSet()
    val feats get() = setOf(background.originFeat)
    val proficiencyBonus get() = classes.maxOfOrNull { it.proficiencyBonus } ?: 0
    val skillProficiencies get() = chosenSkillProficiencies + background.skillProficiencies
    val toolProficiencies get() = background.toolProficiencies
    val savingThrows get() = classes.firstOrNull()?.savingThrows.orEmpty()

    fun isProficient(skill: Skill) = skillProficiencies.any { it.skill == skill }

    fun levelOf(characterClass: CharacterClass) = classes.firstOrNull { CharacterClass.of(it) == characterClass }?.level?.toInt()

    internal fun levelUp(characterClass: CharacterClass, next: PlayableClass) =
        copy(classes = classes.map { if (CharacterClass.of(it) == characterClass) next else it })
}
