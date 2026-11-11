package app.roleparty.campaign.creature.infrastructure.adapter.secondary

import app.roleparty.campaign.creature.domain.model.AbilityScores
import app.roleparty.campaign.creature.domain.model.ArmorClass
import app.roleparty.campaign.creature.domain.model.ChallengeRating
import app.roleparty.campaign.creature.domain.model.Condition
import app.roleparty.campaign.creature.domain.model.CreatureAlignment
import app.roleparty.campaign.creature.domain.model.CreatureLanguage
import app.roleparty.campaign.creature.domain.model.CreatureSize
import app.roleparty.campaign.creature.domain.model.CreatureSkill
import app.roleparty.campaign.creature.domain.model.CreatureType
import app.roleparty.campaign.creature.domain.model.DamageType
import app.roleparty.campaign.creature.domain.model.Defenses
import app.roleparty.campaign.creature.domain.model.DiceExpression
import app.roleparty.campaign.creature.domain.model.Feature
import app.roleparty.campaign.creature.domain.model.Habitat
import app.roleparty.campaign.creature.domain.model.HitPoints
import app.roleparty.campaign.creature.domain.model.InitiativeProficiency
import app.roleparty.campaign.creature.domain.model.Languages
import app.roleparty.campaign.creature.domain.model.LegendaryAction
import app.roleparty.campaign.creature.domain.model.LegendaryActions
import app.roleparty.campaign.creature.domain.model.MovementMode
import app.roleparty.campaign.creature.domain.model.SavingThrows
import app.roleparty.campaign.creature.domain.model.Sense
import app.roleparty.campaign.creature.domain.model.StatBlock
import app.roleparty.campaign.creature.domain.model.Taxonomy
import app.roleparty.campaign.creature.domain.model.Treasure
import app.roleparty.campaign.database.MonsterSheetContent
import app.roleparty.campaign.database.MonsterSheetContent.FeatureContent
import app.roleparty.campaign.database.MonsterSheetContent.LegendaryActionContent

internal fun StatBlock.content() = MonsterSheetContent(
    armorClass = armorClass.value,
    armorClassDetail = armorClass.detail,
    hitPointMax = hitPoints.max,
    hitDice = hitPoints.hitDice.toString(),
    speeds = speeds.mapKeys { it.key.name },
    abilities = abilities.all,
    savingThrows = savingThrows.all,
    skills = skills.mapKeys { it.key.name },
    passivePerception = passivePerception,
    languages = languages.known.map { it.name },
    telepathyFeet = languages.telepathyFeet,
    traits = traits.map { FeatureContent(it.name, it.description) },
    actions = actions.map { FeatureContent(it.name, it.description) },
    bonusActions = bonusActions.map { FeatureContent(it.name, it.description) },
    reactions = reactions.map { FeatureContent(it.name, it.description) },
    legendaryDescription = legendaryActions.description,
    legendaryActions = legendaryActions.items.map { LegendaryActionContent(it.name, it.description, it.cost) },
    legendaryUses = legendaryActions.uses,
    legendaryUsesInLair = legendaryActions.usesInLair,
    alternativeSize = taxonomy.alternativeSize?.name,
    swarmOf = taxonomy.swarmOf?.name,
    typeTags = taxonomy.tags,
    alignment = alignment.name,
    initiativeProficiency = initiative.name,
    hover = hover,
    senses = senses.mapKeys { it.key.name },
    vulnerabilities = defenses.vulnerabilities.names(),
    resistances = defenses.resistances.names(),
    immunities = defenses.immunities.names(),
    conditionImmunities = defenses.conditionImmunities.names(),
    understoodLanguages = languages.understood.names(),
    otherLanguages = languages.other,
    gear = gear,
    lair = lair,
    habitats = habitats.names(),
    treasures = treasures.names(),
)

private fun Set<Enum<*>>.names() = map { it.name }.sorted()

internal fun statBlockOf(size: String, type: String, challengeRating: String, content: MonsterSheetContent): StatBlock {
    val abilities = content.abilities
    return StatBlock(
        size = CreatureSize.valueOf(size),
        type = CreatureType.valueOf(type),
        challengeRating = ChallengeRating.valueOf(challengeRating),
        armorClass = ArmorClass(content.armorClass, content.armorClassDetail),
        hitPoints = HitPoints(content.hitPointMax, checkNotNull(DiceExpression.parse(content.hitDice))),
        speeds = content.speeds.mapKeys { MovementMode.valueOf(it.key) },
        abilities = AbilityScores(abilities[0], abilities[1], abilities[2], abilities[3], abilities[4], abilities[5]),
        savingThrows = with(content.savingThrows) { SavingThrows(get(0), get(1), get(2), get(3), get(4), get(5)) },
        skills = content.skills.mapKeys { CreatureSkill.valueOf(it.key) },
        passivePerception = content.passivePerception,
        languages = Languages(
            known = content.languages.map(CreatureLanguage::valueOf).toSet(),
            telepathyFeet = content.telepathyFeet,
            understood = content.understoodLanguages.map(CreatureLanguage::valueOf).toSet(),
            other = content.otherLanguages,
        ),
        traits = content.traits.map { Feature(it.name, it.description) },
        actions = content.actions.map { Feature(it.name, it.description) },
        bonusActions = content.bonusActions.map { Feature(it.name, it.description) },
        reactions = content.reactions.map { Feature(it.name, it.description) },
        legendaryActions = LegendaryActions(
            description = content.legendaryDescription,
            items = content.legendaryActions.map { LegendaryAction(it.name, it.description, it.cost) },
            uses = content.legendaryUses,
            usesInLair = content.legendaryUsesInLair,
        ),
        taxonomy = Taxonomy(content.alternativeSize?.let(CreatureSize::valueOf), content.swarmOf?.let(CreatureSize::valueOf), content.typeTags),
        alignment = CreatureAlignment.valueOf(content.alignment),
        initiative = InitiativeProficiency.valueOf(content.initiativeProficiency),
        hover = content.hover,
        senses = content.senses.mapKeys { Sense.valueOf(it.key) },
        defenses = Defenses(
            vulnerabilities = content.vulnerabilities.map(DamageType::valueOf).toSet(),
            resistances = content.resistances.map(DamageType::valueOf).toSet(),
            immunities = content.immunities.map(DamageType::valueOf).toSet(),
            conditionImmunities = content.conditionImmunities.map(Condition::valueOf).toSet(),
        ),
        gear = content.gear,
        lair = content.lair,
        habitats = content.habitats.map(Habitat::valueOf).toSet(),
        treasures = content.treasures.map(Treasure::valueOf).toSet(),
    )
}
