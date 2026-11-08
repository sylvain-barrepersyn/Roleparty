package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
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
import app.roleparty.campaign.creature.domain.model.MovementMode
import app.roleparty.campaign.creature.domain.model.Condition
import app.roleparty.campaign.creature.domain.model.CreatureAlignment
import app.roleparty.campaign.creature.domain.model.DamageType
import app.roleparty.campaign.creature.domain.model.Defenses
import app.roleparty.campaign.creature.domain.model.Habitat
import app.roleparty.campaign.creature.domain.model.InitiativeProficiency
import app.roleparty.campaign.creature.domain.model.Sense
import app.roleparty.campaign.creature.domain.model.Taxonomy
import app.roleparty.campaign.creature.domain.model.Treasure
import app.roleparty.media.commons.ImageId

object CreatureSamples {

    fun dragon(campaignId: CampaignId, id: MonsterSheetId = MonsterSheetId.generate()) = MonsterSheet(
        id = id,
        campaignId = campaignId,
        name = "Young Red Dragon",
        image = ImageId.generate(),
        size = CreatureSize.LARGE,
        type = CreatureType.DRAGON,
        challengeRating = ChallengeRating.CR_10,
        armorClass = ArmorClass(18, "natural armor"),
        hitPoints = HitPoints(178, DiceExpression(17, 10, 85)),
        speeds = mapOf(MovementMode.WALK to 40, MovementMode.CLIMB to 40, MovementMode.FLY to 80),
        abilities = AbilityScores(23, 10, 21, 14, 11, 19),
        savingThrows = SavingThrows(dexterity = 4, constitution = 4, wisdom = 4, charisma = 4),
        skills = mapOf(CreatureSkill.PERCEPTION to 8, CreatureSkill.STEALTH to 4),
        passivePerception = 18,
        languages = Languages(setOf(CreatureLanguage.COMMON, CreatureLanguage.DRACONIC), telepathyFeet = 60, setOf(CreatureLanguage.INFERNAL), "one other"),
        traits = listOf(Feature("Fire Resistance", "Immune to fire.")),
        actions = listOf(Feature("Bite", "Melee attack."), Feature("Fire Breath", "Cone of fire.")),
        bonusActions = listOf(Feature("Wing Buffet", "Push back.")),
        reactions = listOf(Feature("Tail Swipe", "When hit.")),
        legendaryActions = LegendaryActions("Three per round.", listOf(LegendaryAction("Detect", "Perception check.", 1)), uses = 3, usesInLair = 4),
        taxonomy = Taxonomy(alternativeSize = CreatureSize.HUGE, tags = "Chromatic"),
        alignment = CreatureAlignment.CHAOTIC_EVIL,
        initiative = InitiativeProficiency.PROFICIENT,
        hover = true,
        senses = mapOf(Sense.BLINDSIGHT to 30, Sense.DARKVISION to 120),
        defenses = Defenses(
            vulnerabilities = setOf(DamageType.COLD),
            resistances = setOf(DamageType.LIGHTNING),
            immunities = setOf(DamageType.FIRE),
            conditionImmunities = setOf(Condition.FRIGHTENED, Condition.CHARMED),
        ),
        gear = listOf("Crown of embers"),
        lair = true,
        habitats = setOf(Habitat.MOUNTAIN, Habitat.HILL),
        treasures = setOf(Treasure.ARMAMENTS),
    )
}
