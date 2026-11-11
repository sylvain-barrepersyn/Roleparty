package app.roleparty.campaign.creature.domain.model.playercharacter

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.media.commons.ImageId

data class PlayerCharacterCreation(
    val campaignId: CampaignId,
    val name: String,
    val image: ImageId,
    val className: String,
    val hitDie: HitDie,
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
)
