package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Fighter(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 10,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT, ArmorCategory.MEDIUM, ArmorCategory.HEAVY, ArmorCategory.SHIELD),
    savingThrows = setOf(SavingThrowProficiency.StrengthSave, SavingThrowProficiency.ConstitutionSave),
    proficiencyBonus = proficiencyBonus
) {
    data class SecondWind(val uses: Int) : Feature
    data class ActionSurge(val uses: Int) : Feature
    data class Indomitable(val uses: Int) : Feature
    data object FightingStyle : Feature
    data object TacticalMind : Feature
    data object TacticalShift : Feature
    data object StudiedAttacks : Feature
    data object EpicBoon : Feature
    data object ImprovedCritical : Feature
    data object RemarkableAthlete : Feature
    data object AdditionalFightingStyle : Feature
    data object SuperiorCritical : Feature
    data object Survivor : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 15,
            dexterity = 14,
            constitution = 13,
            intelligence = 8,
            wisdom = 10,
            charisma = 12
        )
    }
}

class Fighter1 : Fighter(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(FightingStyle, SecondWind(uses = 2), TacticalMind)
)

class Fighter2 : Fighter(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(FightingStyle, SecondWind(uses = 2), TacticalMind, ActionSurge(uses = 1))
)

class ChampionFighter3 : Fighter(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(FightingStyle, SecondWind(uses = 2), TacticalMind, ActionSurge(uses = 1), ImprovedCritical)
)

class ChampionFighter4 : Fighter(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(FightingStyle, SecondWind(uses = 3), TacticalMind, ActionSurge(uses = 1), ImprovedCritical)
)

class ChampionFighter5 : Fighter(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(FightingStyle, SecondWind(uses = 3), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 2))
)

class ChampionFighter6 : Fighter(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 2))
)

class ChampionFighter7 : Fighter(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 2), RemarkableAthlete, Indomitable(uses = 1))
)

class ChampionFighter8 : Fighter(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 2), RemarkableAthlete, Indomitable(uses = 1))
)

class ChampionFighter9 : Fighter(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 2), RemarkableAthlete, Indomitable(uses = 2), TacticalShift)
)

class ChampionFighter10 : Fighter(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 2), RemarkableAthlete, Indomitable(uses = 2), TacticalShift, AdditionalFightingStyle)
)

class ChampionFighter11 : Fighter(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 2), TacticalShift, AdditionalFightingStyle)
)

class ChampionFighter12 : Fighter(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 2), TacticalShift, AdditionalFightingStyle)
)

class ChampionFighter13 : Fighter(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle)
)

class ChampionFighter14 : Fighter(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle)
)

class ChampionFighter15 : Fighter(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle, SuperiorCritical)
)

class ChampionFighter16 : Fighter(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 1), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle, SuperiorCritical)
)

class ChampionFighter17 : Fighter(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 2), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle, SuperiorCritical, StudiedAttacks)
)

class ChampionFighter18 : Fighter(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 2), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle, SuperiorCritical, StudiedAttacks, Survivor)
)

class ChampionFighter19 : Fighter(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 2), ImprovedCritical, ExtraAttack(numberOfAttacks = 3), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle, SuperiorCritical, StudiedAttacks, Survivor, EpicBoon)
)

class ChampionFighter20 : Fighter(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(FightingStyle, SecondWind(uses = 4), TacticalMind, ActionSurge(uses = 2), ImprovedCritical, ExtraAttack(numberOfAttacks = 4), RemarkableAthlete, Indomitable(uses = 3), TacticalShift, AdditionalFightingStyle, SuperiorCritical, StudiedAttacks, Survivor, EpicBoon)
)
