package app.roleparty.campaign.domain.model.playercharacter

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.media.commons.ImageId

data class PlayerCharacterCreation(
    val campaignId: CampaignId,
    val name: String,
    val image: ImageId,
    val characterClass: CharacterClass,
    val alignment: Alignment,
    val abilityScores: AbilityScores,
    val species: Species,
    val background: Background,
    val appearance: String,
    val backstory: String,
    val skillProficiencies: Set<SkillProficiency>,
    val languages: Set<Language>,
)
