package app.roleparty.campaign.creature.infrastructure.adapter.primary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.contract.web.ChallengeRatingName
import app.roleparty.campaign.creature.contract.web.CreatureLanguageName
import app.roleparty.campaign.creature.contract.web.CreatureSizeName
import app.roleparty.campaign.creature.contract.web.CreatureSkillName
import app.roleparty.campaign.creature.contract.web.CreatureTypeName
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetResponseBody
import app.roleparty.campaign.creature.contract.web.MovementModeName
import app.roleparty.campaign.creature.domain.model.AbilityScores
import app.roleparty.campaign.creature.domain.model.CreatureSkill
import app.roleparty.campaign.creature.domain.model.SavingThrows
import app.roleparty.campaign.creature.domain.model.ArmorClass
import app.roleparty.campaign.creature.domain.model.ChallengeRating
import app.roleparty.campaign.creature.domain.model.CreatureLanguage
import app.roleparty.campaign.creature.domain.model.CreatureSize
import app.roleparty.campaign.creature.domain.model.CreatureType
import app.roleparty.campaign.creature.domain.model.DiceExpression
import app.roleparty.campaign.creature.domain.model.Feature
import app.roleparty.campaign.creature.domain.model.HitPoints
import app.roleparty.campaign.creature.domain.model.Languages
import app.roleparty.campaign.creature.domain.model.LegendaryAction
import app.roleparty.campaign.creature.domain.model.LegendaryActions
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MonsterSheetOverview
import app.roleparty.campaign.creature.domain.model.MovementMode
import app.roleparty.campaign.creature.contract.web.ConditionName
import app.roleparty.campaign.creature.contract.web.CreatureAlignmentName
import app.roleparty.campaign.creature.contract.web.DamageTypeName
import app.roleparty.campaign.creature.contract.web.HabitatName
import app.roleparty.campaign.creature.contract.web.InitiativeProficiencyName
import app.roleparty.campaign.creature.contract.web.SenseName
import app.roleparty.campaign.creature.contract.web.TreasureName
import app.roleparty.campaign.creature.domain.model.Condition
import app.roleparty.campaign.creature.domain.model.CreatureAlignment
import app.roleparty.campaign.creature.domain.model.DamageType
import app.roleparty.campaign.creature.domain.model.Defenses
import app.roleparty.campaign.creature.domain.model.Habitat
import app.roleparty.campaign.creature.domain.model.InitiativeProficiency
import app.roleparty.campaign.creature.domain.model.Sense
import app.roleparty.campaign.creature.domain.model.Taxonomy
import app.roleparty.campaign.creature.domain.model.Treasure

internal fun MonsterSheetOverview.toResponseBody() = MonsterSheetOverviewResponseBody(
    id, name, image, CreatureTypeName.valueOf(type.name), CreatureSizeName.valueOf(size.name), ChallengeRatingName.valueOf(challengeRating.name),
)

internal fun MonsterSheet.toResponseBody() = MonsterSheetResponseBody(
    id,
    MonsterSheetBody(
        name = name,
        imageId = image,
        size = CreatureSizeName.valueOf(size.name),
        type = CreatureTypeName.valueOf(type.name),
        challengeRating = ChallengeRatingName.valueOf(challengeRating.name),
        armorClass = MonsterSheetBody.ArmorClassBody(armorClass.value, armorClass.detail),
        hitPoints = MonsterSheetBody.HitPointsBody(hitPoints.max, hitPoints.hitDice.toString()),
        speeds = speeds.mapKeys { MovementModeName.valueOf(it.key.name) },
        abilities = with(abilities) { MonsterSheetBody.AbilitiesBody(strength, dexterity, constitution, intelligence, wisdom, charisma) },
        savingThrows = with(savingThrows) { MonsterSheetBody.SavingThrowsBody(strength, dexterity, constitution, intelligence, wisdom, charisma) },
        skills = skills.mapKeys { CreatureSkillName.valueOf(it.key.name) },
        passivePerception = passivePerception,
        languages = MonsterSheetBody.LanguagesBody(
            known = languages.known.map { CreatureLanguageName.valueOf(it.name) }.toSet(),
            telepathyFeet = languages.telepathyFeet,
            understood = languages.understood.map { CreatureLanguageName.valueOf(it.name) }.toSet(),
            other = languages.other,
        ),
        traits = traits.map { it.toBody() },
        actions = actions.map { it.toBody() },
        bonusActions = bonusActions.map { it.toBody() },
        reactions = reactions.map { it.toBody() },
        legendaryActions = MonsterSheetBody.LegendaryActionsBody(
            legendaryActions.description,
            legendaryActions.items.map { MonsterSheetBody.LegendaryActionBody(it.name, it.description, it.cost) },
            legendaryActions.uses,
            legendaryActions.usesInLair,
        ),
        taxonomy = MonsterSheetBody.TaxonomyBody(
            taxonomy.alternativeSize?.let { CreatureSizeName.valueOf(it.name) },
            taxonomy.swarmOf?.let { CreatureSizeName.valueOf(it.name) },
            taxonomy.tags,
        ),
        alignment = CreatureAlignmentName.valueOf(alignment.name),
        initiative = InitiativeProficiencyName.valueOf(initiative.name),
        hover = hover,
        senses = senses.mapKeys { SenseName.valueOf(it.key.name) },
        defenses = with(defenses) {
            MonsterSheetBody.DefensesBody(
                vulnerabilities.map { DamageTypeName.valueOf(it.name) }.toSet(),
                resistances.map { DamageTypeName.valueOf(it.name) }.toSet(),
                immunities.map { DamageTypeName.valueOf(it.name) }.toSet(),
                conditionImmunities.map { ConditionName.valueOf(it.name) }.toSet(),
            )
        },
        gear = gear,
        lair = lair,
        habitats = habitats.map { HabitatName.valueOf(it.name) }.toSet(),
        treasures = treasures.map { TreasureName.valueOf(it.name) }.toSet(),
    ),
)

internal fun MonsterSheetBody.toMonsterSheet(id: MonsterSheetId, campaignId: CampaignId): MonsterSheet? = MonsterSheet(
    id = id,
    campaignId = campaignId,
    name = name,
    image = imageId,
    size = CreatureSize.valueOf(size.name),
    type = CreatureType.valueOf(type.name),
    challengeRating = ChallengeRating.valueOf(challengeRating.name),
    armorClass = ArmorClass(armorClass.value, armorClass.detail?.takeIf { it.isNotBlank() }),
    hitPoints = HitPoints(hitPoints.max, DiceExpression.parse(hitPoints.dice) ?: return null),
    speeds = speeds.mapKeys { MovementMode.valueOf(it.key.name) },
    abilities = with(abilities) { AbilityScores(strength, dexterity, constitution, intelligence, wisdom, charisma) },
    savingThrows = with(savingThrows) { SavingThrows(strength, dexterity, constitution, intelligence, wisdom, charisma) },
    skills = skills.mapKeys { CreatureSkill.valueOf(it.key.name) },
    passivePerception = passivePerception,
    languages = Languages(
        known = languages.known.map { CreatureLanguage.valueOf(it.name) }.toSet(),
        telepathyFeet = languages.telepathyFeet,
        understood = languages.understood.map { CreatureLanguage.valueOf(it.name) }.toSet(),
        other = languages.other,
    ),
    traits = traits.map { it.toFeature() },
    actions = actions.map { it.toFeature() },
    bonusActions = bonusActions.map { it.toFeature() },
    reactions = reactions.map { it.toFeature() },
    legendaryActions = LegendaryActions(
        legendaryActions.description,
        legendaryActions.items.map { LegendaryAction(it.name, it.description, it.cost) },
        legendaryActions.uses,
        legendaryActions.usesInLair,
    ),
    taxonomy = Taxonomy(taxonomy.alternativeSize?.let { CreatureSize.valueOf(it.name) }, taxonomy.swarmOf?.let { CreatureSize.valueOf(it.name) }, taxonomy.tags),
    alignment = CreatureAlignment.valueOf(alignment.name),
    initiative = InitiativeProficiency.valueOf(initiative.name),
    hover = hover,
    senses = senses.mapKeys { Sense.valueOf(it.key.name) },
    defenses = with(defenses) {
        Defenses(
            vulnerabilities.map { DamageType.valueOf(it.name) }.toSet(),
            resistances.map { DamageType.valueOf(it.name) }.toSet(),
            immunities.map { DamageType.valueOf(it.name) }.toSet(),
            conditionImmunities.map { Condition.valueOf(it.name) }.toSet(),
        )
    },
    gear = gear,
    lair = lair,
    habitats = habitats.map { Habitat.valueOf(it.name) }.toSet(),
    treasures = treasures.map { Treasure.valueOf(it.name) }.toSet(),
)

private fun Feature.toBody() = MonsterSheetBody.FeatureBody(name, description)

private fun MonsterSheetBody.FeatureBody.toFeature() = Feature(name, description)
