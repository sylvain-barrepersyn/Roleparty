package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.playercharacter.Ability
import app.roleparty.campaign.creature.domain.model.playercharacter.AbilityScores as PlayerCharacterAbilityScores
import app.roleparty.campaign.creature.domain.model.playercharacter.Alignment
import app.roleparty.campaign.creature.domain.model.playercharacter.ArmorTraining
import app.roleparty.campaign.creature.domain.model.playercharacter.Background
import app.roleparty.campaign.creature.domain.model.playercharacter.CharacterClass
import app.roleparty.campaign.creature.domain.model.playercharacter.HitDie
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterDetails
import app.roleparty.campaign.creature.domain.model.playercharacter.Size
import app.roleparty.campaign.creature.domain.model.playercharacter.Skill
import app.roleparty.campaign.creature.domain.model.playercharacter.Species
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
import app.roleparty.campaign.creature.domain.model.StatBlock
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
        stats = dragonStats(),
    )

    fun dragonStats() = StatBlock(
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

    fun playerCharacterCreation(campaignId: CampaignId, className: String = "Runesmith") = PlayerCharacterCreation(
        campaignId = campaignId,
        name = "Gimli",
        image = ImageId.generate(),
        className = className,
        hitDie = HitDie.D10,
        classFeatures = "Rune carving.",
        savingThrows = setOf(Ability.STRENGTH, Ability.CONSTITUTION),
        armorTrainings = setOf(ArmorTraining.LIGHT, ArmorTraining.MEDIUM, ArmorTraining.SHIELDS),
        background = Background("Mountain guide", "Tough", "Cartographer's tools"),
        skillProficiencies = setOf(Skill.ATHLETICS, Skill.PERCEPTION, Skill.SURVIVAL, Skill.HISTORY),
        species = Species("Stone dwarf", Size.MEDIUM, 30, "Darkvision, stonecunning."),
        languages = listOf("Common", "Dwarvish", "Giant"),
        equipment = "Warhammer, chain shirt, explorer's pack.",
        coins = 12,
        abilityScores = PlayerCharacterAbilityScores(15, 13, 14, 10, 12, 8),
        alignment = Alignment.LAWFUL_GOOD,
        details = PlayerCharacterDetails("Bearded.", "Stubborn.", "From the mountains.", "Fears the sea."),
    )

    fun playerCharacter(campaignId: CampaignId, player: AccountId): PlayerCharacter {
        val creation = playerCharacterCreation(campaignId)
        return PlayerCharacter(
            id = PlayerCharacterId.generate(),
            campaignId = campaignId,
            player = player,
            name = creation.name,
            image = creation.image,
            classes = listOf(CharacterClass(creation.className, 1, creation.hitDie)),
            classFeatures = creation.classFeatures,
            savingThrows = creation.savingThrows,
            armorTrainings = creation.armorTrainings,
            background = creation.background,
            skillProficiencies = creation.skillProficiencies,
            species = creation.species,
            languages = creation.languages,
            equipment = creation.equipment,
            coins = creation.coins,
            abilityScores = creation.abilityScores,
            alignment = creation.alignment,
            details = creation.details,
        )
    }
}
