package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.Alignment
import app.roleparty.campaign.domain.model.playercharacter.Background
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.campaign.domain.model.playercharacter.SkillProficiency
import app.roleparty.campaign.domain.model.playercharacter.Species
import app.roleparty.campaign.domain.model.playercharacter.StandardLanguage
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.domain.model.playercharacter.ArtisanToolProficiency
import app.roleparty.media.commons.ImageId

object CampaignSamples {

    val SKILLS = listOf(
        SkillProficiency.Athletics,
        SkillProficiency.Perception,
        SkillProficiency.Stealth,
        SkillProficiency.Arcana,
        SkillProficiency.History,
    )

    fun campaign(gameMaster: AccountId = AccountId.generate()) =
        Campaign(CampaignId.generate(), "The Lost Mine", "Goblins and caves.", ImageId.generate(), gameMaster)

    fun creation(campaignId: CampaignId, characterClass: CharacterClass = CharacterClass.FIGHTER) = PlayerCharacterCreation(
        campaignId = campaignId,
        name = "Gimli",
        image = ImageId.generate(),
        characterClass = characterClass,
        alignment = Alignment.LAWFUL_GOOD,
        abilityScores = AbilityScores(15, 13, 14, 10, 12, 8),
        species = Species.Dwarf,
        background = Background.Artisan(ArtisanToolProficiency.SmithsTools),
        appearance = "Bearded.",
        backstory = "From the mountains.",
        skillProficiencies = SKILLS.take(characterClass.skillCount).toSet(),
        languages = setOf(StandardLanguage.COMMON, StandardLanguage.DWARVISH),
    )

    fun playerCharacter(campaignId: CampaignId, player: AccountId, characterClass: CharacterClass = CharacterClass.FIGHTER): PlayerCharacter {
        val creation = creation(campaignId, characterClass)
        return PlayerCharacter(
            id = PlayerCharacterId.generate(),
            campaignId = campaignId,
            player = player,
            name = creation.name,
            image = creation.image,
            alignment = creation.alignment,
            abilityScores = creation.abilityScores,
            species = Species.Human(Species.Human.Size.SMALL),
            background = creation.background,
            appearance = creation.appearance,
            backstory = creation.backstory,
            classes = listOfNotNull(characterClass.atLevel(1)),
            chosenSkillProficiencies = creation.skillProficiencies,
            languages = creation.languages,
        )
    }
}
