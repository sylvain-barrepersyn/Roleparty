package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Ranger(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 10,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT, ArmorCategory.MEDIUM, ArmorCategory.SHIELD),
    savingThrows = setOf(SavingThrowProficiency.StrengthSave, SavingThrowProficiency.DexteritySave),
    proficiencyBonus = proficiencyBonus
) {
    data object FavoredEnemy : Feature
    data object WildSense : Feature
    data object Roving : Feature
    data object TirelessStalker : Feature
    data object NatureSurge : Feature
    data object FoeBreaker : Feature
    data object EpicBoon : Feature
    data object PrimalCompanion : Feature
    data object ExceptionalTraining : Feature
    data object BestialFury : Feature
    data object ShareSpells : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 12,
            dexterity = 15,
            constitution = 13,
            intelligence = 8,
            wisdom = 14,
            charisma = 10
        )
    }
}

class Ranger1 : Ranger(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(FavoredEnemy, WildSense)
)

class Ranger2 : Ranger(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(2))
)

class BeastMasterRanger3 : Ranger(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(3), PrimalCompanion)
)

class BeastMasterRanger4 : Ranger(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(4), PrimalCompanion)
)

class BeastMasterRanger5 : Ranger(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(5), PrimalCompanion, ExtraAttack(numberOfAttacks = 2))
)

class BeastMasterRanger6 : Ranger(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(6), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving)
)

class BeastMasterRanger7 : Ranger(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(7), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining)
)

class BeastMasterRanger8 : Ranger(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(8), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining)
)

class BeastMasterRanger9 : Ranger(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(9), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker)
)

class BeastMasterRanger10 : Ranger(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(10), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge)
)

class BeastMasterRanger11 : Ranger(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(11), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury)
)

class BeastMasterRanger12 : Ranger(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(12), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury)
)

class BeastMasterRanger13 : Ranger(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(13), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury)
)

class BeastMasterRanger14 : Ranger(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(14), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury, FoeBreaker)
)

class BeastMasterRanger15 : Ranger(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(15), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury, FoeBreaker, ShareSpells)
)

class BeastMasterRanger16 : Ranger(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(16), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury, FoeBreaker, ShareSpells)
)

class BeastMasterRanger17 : Ranger(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(17), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury, FoeBreaker, ShareSpells)
)

class BeastMasterRanger18 : Ranger(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(18), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury, FoeBreaker, ShareSpells)
)

class BeastMasterRanger19 : Ranger(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(19), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury, FoeBreaker, ShareSpells, EpicBoon)
)

class BeastMasterRanger20 : Ranger(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(FavoredEnemy, WildSense, halfCasterSlots(20), PrimalCompanion, ExtraAttack(numberOfAttacks = 2), Roving, ExceptionalTraining, TirelessStalker, NatureSurge, BestialFury, FoeBreaker, ShareSpells, EpicBoon)
)
