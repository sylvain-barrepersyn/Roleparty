package app.roleparty.campaign.creature.infrastructure.adapter.primary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.contract.web.AbilityName
import app.roleparty.campaign.creature.contract.web.AbilityScoresBody
import app.roleparty.campaign.creature.contract.web.AlignmentName
import app.roleparty.campaign.creature.contract.web.ArmorTrainingName
import app.roleparty.campaign.creature.contract.web.BackgroundBody
import app.roleparty.campaign.creature.contract.web.ClassLevelBody
import app.roleparty.campaign.creature.contract.web.HitDieName
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterDetailsBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.creature.contract.web.SizeName
import app.roleparty.campaign.creature.contract.web.SkillName
import app.roleparty.campaign.creature.contract.web.SpeciesBody
import app.roleparty.campaign.creature.domain.model.playercharacter.Ability
import app.roleparty.campaign.creature.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.creature.domain.model.playercharacter.Alignment
import app.roleparty.campaign.creature.domain.model.playercharacter.ArmorTraining
import app.roleparty.campaign.creature.domain.model.playercharacter.Background
import app.roleparty.campaign.creature.domain.model.playercharacter.CharacterClass
import app.roleparty.campaign.creature.domain.model.playercharacter.HitDie
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterDetails
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterOverview
import app.roleparty.campaign.creature.domain.model.playercharacter.Size
import app.roleparty.campaign.creature.domain.model.playercharacter.Skill
import app.roleparty.campaign.creature.domain.model.playercharacter.Species

internal fun PlayerCharacterCreationRequestBody.toCreation(campaignId: CampaignId) = PlayerCharacterCreation(
    campaignId = campaignId,
    name = name,
    image = imageId,
    className = className,
    hitDie = HitDie.valueOf(hitDie.name),
    classFeatures = classFeatures,
    savingThrows = savingThrows.map { Ability.valueOf(it.name) }.toSet(),
    armorTrainings = armorTrainings.map { ArmorTraining.valueOf(it.name) }.toSet(),
    background = Background(background.name, background.originFeat, background.toolProficiency),
    skillProficiencies = skills.map { Skill.valueOf(it.name) }.toSet(),
    species = Species(species.name, Size.valueOf(species.size.name), species.speed, species.traits),
    languages = languages,
    equipment = equipment,
    coins = coins,
    abilityScores = with(abilityScores) { AbilityScores(strength, dexterity, constitution, intelligence, wisdom, charisma) },
    alignment = Alignment.valueOf(alignment.name),
    details = PlayerCharacterDetails(details.appearance, details.personality, details.backstory, details.other),
)

private fun CharacterClass.toBody() = ClassLevelBody(name, level, HitDieName.valueOf(hitDie.name))

internal fun PlayerCharacterOverview.toResponseBody() =
    PlayerCharacterOverviewResponseBody(id, name, image, classes.map { it.toBody() }, species, level)

internal fun PlayerCharacter.toResponseBody() = PlayerCharacterResponseBody(
    id = id,
    campaignId = campaignId,
    player = player,
    name = name,
    imageId = image,
    classes = classes.map { it.toBody() },
    level = level,
    proficiencyBonus = proficiencyBonus,
    maxHitPoints = maxHitPoints,
    armorClass = armorClass,
    initiative = initiative,
    passivePerception = passivePerception,
    classFeatures = classFeatures,
    savingThrows = savingThrows.map { AbilityName.valueOf(it.name) }.toSet(),
    armorTrainings = armorTrainings.map { ArmorTrainingName.valueOf(it.name) }.toSet(),
    background = BackgroundBody(background.name, background.originFeat, background.toolProficiency),
    skills = skillProficiencies.map { SkillName.valueOf(it.name) }.toSet(),
    species = SpeciesBody(species.name, SizeName.valueOf(species.size.name), species.speed, species.traits),
    languages = languages,
    equipment = equipment,
    coins = coins,
    abilityScores = with(abilityScores) { AbilityScoresBody(strength, dexterity, constitution, intelligence, wisdom, charisma) },
    alignment = AlignmentName.valueOf(alignment.name),
    details = PlayerCharacterDetailsBody(details.appearance, details.personality, details.backstory, details.other),
)
