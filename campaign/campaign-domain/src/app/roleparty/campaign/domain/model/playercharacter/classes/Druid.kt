package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Druid(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 8,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT, ArmorCategory.MEDIUM, ArmorCategory.SHIELD),
    savingThrows = setOf(SavingThrowProficiency.IntelligenceSave, SavingThrowProficiency.WisdomSave),
    proficiencyBonus = proficiencyBonus
) {
    data class WildShape(val uses: Int) : Feature
    data object Druidic : Feature
    data object PrimalOrder : Feature
    data object WildResurgence : Feature
    data object ElementalFury : Feature
    data object BeastSpells : Feature
    data object ArchDruid : Feature
    data object EpicBoon : Feature
    data object LandSpells : Feature
    data object NaturalRecovery : Feature
    data object NaturesSanctuary : Feature
    data object NatureSamplings : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 8,
            dexterity = 12,
            constitution = 14,
            intelligence = 13,
            wisdom = 15,
            charisma = 10
        )
    }
}

class Druid1 : Druid(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(1))
)

class Druid2 : Druid(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(2), WildShape(uses = 2), WildResurgence)
)

class LandDruid3 : Druid(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(3), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery)
)

class LandDruid4 : Druid(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(4), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery)
)

class LandDruid5 : Druid(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(5), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery)
)

class LandDruid6 : Druid(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(6), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings)
)

class LandDruid7 : Druid(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(7), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings)
)

class LandDruid8 : Druid(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(8), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings)
)

class LandDruid9 : Druid(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(9), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings)
)

class LandDruid10 : Druid(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(10), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid11 : Druid(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(11), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid12 : Druid(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(12), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid13 : Druid(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(13), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid14 : Druid(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(14), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid15 : Druid(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(15), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid16 : Druid(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(16), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid17 : Druid(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(17), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary)
)

class LandDruid18 : Druid(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(18), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary, BeastSpells, ElementalFury)
)

class LandDruid19 : Druid(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(19), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary, BeastSpells, ElementalFury, EpicBoon)
)

class LandDruid20 : Druid(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(Druidic, PrimalOrder, fullCasterSlots(20), WildShape(uses = 2), WildResurgence, LandSpells, NaturalRecovery, NatureSamplings, NaturesSanctuary, BeastSpells, ElementalFury, EpicBoon, ArchDruid)
)
