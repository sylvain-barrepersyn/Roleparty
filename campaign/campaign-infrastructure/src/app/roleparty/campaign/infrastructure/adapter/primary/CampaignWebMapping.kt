package app.roleparty.campaign.infrastructure.adapter.primary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.AbilityScoresBody
import app.roleparty.campaign.contract.web.AlignmentName
import app.roleparty.campaign.contract.web.BackgroundName
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.CampaignResponseBody
import app.roleparty.campaign.contract.web.ClassLevelBody
import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.contract.web.LanguageName
import app.roleparty.campaign.contract.web.PlayableClassName
import app.roleparty.campaign.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.contract.web.PlayerCharacterOverviewResponseBody
import app.roleparty.campaign.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.contract.web.SizeName
import app.roleparty.campaign.contract.web.SkillName
import app.roleparty.campaign.contract.web.SpeciesName
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacterOverview
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.infrastructure.PlayerCharacterCodes

internal fun CampaignOverview.toResponseBody() = CampaignOverviewResponseBody(id, name, image, players.size)

internal fun Campaign.toResponseBody() = CampaignResponseBody(id, name, description, image, gameMaster, players.sortedBy { it.toString() })

internal fun Invitation.toResponseBody() = InvitationResponseBody(code.toString(), used)

internal fun PlayableClassName.toCharacterClass() = CharacterClass.valueOf(name)

internal fun PlayerCharacterCreationRequestBody.toCreation(campaignId: CampaignId): PlayerCharacterCreation? {
    val species = PlayerCharacterCodes.species(species.name, speciesSize?.name) ?: return null
    val background = PlayerCharacterCodes.background(background.name, backgroundTool) ?: return null
    return PlayerCharacterCreation(
        campaignId = campaignId,
        name = name,
        image = imageId,
        characterClass = characterClass.toCharacterClass(),
        alignment = PlayerCharacterCodes.alignment(alignment.name) ?: return null,
        abilityScores = with(abilityScores) { AbilityScores(strength, dexterity, constitution, intelligence, wisdom, charisma) },
        species = species,
        background = background,
        appearance = appearance,
        backstory = backstory,
        skillProficiencies = skills.mapNotNull { PlayerCharacterCodes.skill(it.name) }.toSet(),
        languages = languages.mapNotNull { PlayerCharacterCodes.language(it.name) }.toSet(),
    )
}

internal fun PlayerCharacterOverview.toResponseBody() =
    PlayerCharacterOverviewResponseBody(id, name, image, SpeciesName.valueOf(PlayerCharacterCodes.code(species)), level)

internal fun PlayerCharacter.toResponseBody() = PlayerCharacterResponseBody(
    id = id,
    campaignId = campaignId,
    player = player,
    name = name,
    imageId = image,
    classes = classes.map { ClassLevelBody(PlayableClassName.valueOf(CharacterClass.of(it).name), it.level.toInt()) },
    level = level,
    proficiencyBonus = proficiencyBonus,
    species = SpeciesName.valueOf(PlayerCharacterCodes.code(species)),
    size = SizeName.valueOf(size.name),
    background = BackgroundName.valueOf(PlayerCharacterCodes.code(background)),
    backgroundTool = PlayerCharacterCodes.toolCode(background),
    alignment = AlignmentName.valueOf(PlayerCharacterCodes.code(alignment)),
    abilityScores = with(abilityScores) {
        AbilityScoresBody(strength.value, dexterity.value, constitution.value, intelligence.value, wisdom.value, charisma.value)
    },
    appearance = appearance,
    backstory = backstory,
    skills = skillProficiencies.map { SkillName.valueOf(PlayerCharacterCodes.code(it)) }.toSet(),
    languages = languages.map { LanguageName.valueOf(PlayerCharacterCodes.code(it)) }.toSet(),
)
