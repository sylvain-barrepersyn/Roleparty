package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Bard(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 8,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT),
    savingThrows = setOf(SavingThrowProficiency.DexteritySave, SavingThrowProficiency.CharismaSave),
    proficiencyBonus = proficiencyBonus
) {
    data class BardicInspiration(val dieSides: Int) : Feature
    data class Expertise(val skills: Int) : Feature
    data object JackOfAllTrades : Feature
    data object Countercharm : Feature
    data object FontOfInspiration : Feature
    data object MagicalSecrets : Feature
    data object SuperiorBardicInspiration : Feature
    data object WordsOfCreation : Feature
    data object EpicBoon : Feature
    data object CuttingWords : Feature
    data object MagicalDiscoveries : Feature
    data object PeerlessSkill : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 8,
            dexterity = 14,
            constitution = 12,
            intelligence = 13,
            wisdom = 10,
            charisma = 15
        )
    }
}

class Bard1 : Bard(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(BardicInspiration(dieSides = 6), fullCasterSlots(1))
)

class Bard2 : Bard(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(BardicInspiration(dieSides = 6), fullCasterSlots(2), Expertise(skills = 2), JackOfAllTrades)
)

class LoreBard3 : Bard(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(BardicInspiration(dieSides = 6), fullCasterSlots(3), Expertise(skills = 2), JackOfAllTrades, CuttingWords)
)

class LoreBard4 : Bard(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(BardicInspiration(dieSides = 6), fullCasterSlots(4), Expertise(skills = 2), JackOfAllTrades, CuttingWords)
)

class LoreBard5 : Bard(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(BardicInspiration(dieSides = 8), fullCasterSlots(5), Expertise(skills = 2), JackOfAllTrades, CuttingWords, FontOfInspiration)
)

class LoreBard6 : Bard(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(BardicInspiration(dieSides = 8), fullCasterSlots(6), Expertise(skills = 2), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries)
)

class LoreBard7 : Bard(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(BardicInspiration(dieSides = 8), fullCasterSlots(7), Expertise(skills = 2), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm)
)

class LoreBard8 : Bard(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(BardicInspiration(dieSides = 8), fullCasterSlots(8), Expertise(skills = 2), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm)
)

class LoreBard9 : Bard(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(BardicInspiration(dieSides = 8), fullCasterSlots(9), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm)
)

class LoreBard10 : Bard(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(BardicInspiration(dieSides = 10), fullCasterSlots(10), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets)
)

class LoreBard11 : Bard(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(BardicInspiration(dieSides = 10), fullCasterSlots(11), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets)
)

class LoreBard12 : Bard(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(BardicInspiration(dieSides = 10), fullCasterSlots(12), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets)
)

class LoreBard13 : Bard(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(BardicInspiration(dieSides = 10), fullCasterSlots(13), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets)
)

class LoreBard14 : Bard(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(BardicInspiration(dieSides = 10), fullCasterSlots(14), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets, PeerlessSkill)
)

class LoreBard15 : Bard(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(BardicInspiration(dieSides = 12), fullCasterSlots(15), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets, PeerlessSkill)
)

class LoreBard16 : Bard(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(BardicInspiration(dieSides = 12), fullCasterSlots(16), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets, PeerlessSkill)
)

class LoreBard17 : Bard(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(BardicInspiration(dieSides = 12), fullCasterSlots(17), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets, PeerlessSkill)
)

class LoreBard18 : Bard(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(BardicInspiration(dieSides = 12), fullCasterSlots(18), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets, PeerlessSkill, SuperiorBardicInspiration)
)

class LoreBard19 : Bard(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(BardicInspiration(dieSides = 12), fullCasterSlots(19), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets, PeerlessSkill, SuperiorBardicInspiration, EpicBoon)
)

class LoreBard20 : Bard(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(BardicInspiration(dieSides = 12), fullCasterSlots(20), Expertise(skills = 4), JackOfAllTrades, CuttingWords, FontOfInspiration, MagicalDiscoveries, Countercharm, MagicalSecrets, PeerlessSkill, SuperiorBardicInspiration, EpicBoon, WordsOfCreation)
)
