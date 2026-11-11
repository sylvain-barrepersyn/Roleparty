package app.roleparty.campaign.creature.domain.model.playercharacter

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId

data class PlayerCharacter(
    val id: PlayerCharacterId,
    val campaignId: CampaignId,
    val player: AccountId,
    val name: String,
    val image: ImageId,
    val classes: List<CharacterClass>,
    val classFeatures: String,
    val savingThrows: Set<Ability>,
    val armorTrainings: Set<ArmorTraining>,
    val background: Background,
    val skillProficiencies: Set<Skill>,
    val species: Species,
    val languages: List<String>,
    val equipment: String,
    val coins: Int,
    val abilityScores: AbilityScores,
    val alignment: Alignment,
    val details: PlayerCharacterDetails,
) {
    val level get() = classes.sumOf { it.level }
    val proficiencyBonus get() = 2 + (level - 1) / 4
    val initiative get() = abilityScores.modifier(Ability.DEXTERITY)
    val armorClass get() = 10 + abilityScores.modifier(Ability.DEXTERITY)
    val passivePerception get() = 10 + skillModifier(Skill.PERCEPTION)

    val maxHitPoints: Int
        get() {
            val constitution = abilityScores.modifier(Ability.CONSTITUTION)
            return classes.withIndex().sumOf { indexed ->
                (1..indexed.value.level).sumOf { level ->
                    val die = if (indexed.index == 0 && level == 1) indexed.value.hitDie.faces else indexed.value.hitDie.fixedValue
                    maxOf(1, die + constitution)
                }
            }
        }

    fun savingThrowModifier(ability: Ability) = abilityScores.modifier(ability) + if (ability in savingThrows) proficiencyBonus else 0

    fun skillModifier(skill: Skill) = abilityScores.modifier(skill.ability) + if (skill in skillProficiencies) proficiencyBonus else 0
}
