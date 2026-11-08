package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Rogue(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 8,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT),
    savingThrows = setOf(SavingThrowProficiency.DexteritySave, SavingThrowProficiency.IntelligenceSave),
    proficiencyBonus = proficiencyBonus
) {
    data class SneakAttack(val damageDice: Int) : Feature
    data class Expertise(val skills: Int) : Feature
    data object ThievesCant : Feature
    data object CunningAction : Feature
    data object SteadyAim : Feature
    data object UncannyDodge : Feature
    data object Evasion : Feature
    data object ReliableTalent : Feature
    data object SlipperyMind : Feature
    data object Elusive : Feature
    data object StrokeOfLuck : Feature
    data object EpicBoon : Feature
    data object FastHands : Feature
    data object SecondStoryWork : Feature
    data object SupremeSneak : Feature
    data object UseMagicDevice : Feature
    data object ThiefsReflexes : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 12,
            dexterity = 15,
            constitution = 13,
            intelligence = 14,
            wisdom = 10,
            charisma = 8
        )
    }
}

class Rogue1 : Rogue(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(SneakAttack(damageDice = 1), Expertise(skills = 2), ThievesCant)
)

class Rogue2 : Rogue(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(SneakAttack(damageDice = 1), Expertise(skills = 2), ThievesCant, CunningAction)
)

class ThiefRogue3 : Rogue(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(SneakAttack(damageDice = 2), Expertise(skills = 2), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork)
)

class ThiefRogue4 : Rogue(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(SneakAttack(damageDice = 2), Expertise(skills = 2), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork)
)

class ThiefRogue5 : Rogue(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(SneakAttack(damageDice = 3), Expertise(skills = 2), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge)
)

class ThiefRogue6 : Rogue(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(SneakAttack(damageDice = 3), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge)
)

class ThiefRogue7 : Rogue(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(SneakAttack(damageDice = 4), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion)
)

class ThiefRogue8 : Rogue(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(SneakAttack(damageDice = 4), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion)
)

class ThiefRogue9 : Rogue(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(SneakAttack(damageDice = 5), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak)
)

class ThiefRogue10 : Rogue(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(SneakAttack(damageDice = 5), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak)
)

class ThiefRogue11 : Rogue(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(SneakAttack(damageDice = 6), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent)
)

class ThiefRogue12 : Rogue(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(SneakAttack(damageDice = 6), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent)
)

class ThiefRogue13 : Rogue(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(SneakAttack(damageDice = 7), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice)
)

class ThiefRogue14 : Rogue(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(SneakAttack(damageDice = 7), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice, SlipperyMind)
)

class ThiefRogue15 : Rogue(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(SneakAttack(damageDice = 8), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice, SlipperyMind)
)

class ThiefRogue16 : Rogue(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(SneakAttack(damageDice = 8), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice, SlipperyMind)
)

class ThiefRogue17 : Rogue(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(SneakAttack(damageDice = 9), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice, SlipperyMind, ThiefsReflexes)
)

class ThiefRogue18 : Rogue(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(SneakAttack(damageDice = 9), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice, SlipperyMind, ThiefsReflexes, Elusive)
)

class ThiefRogue19 : Rogue(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(SneakAttack(damageDice = 10), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice, SlipperyMind, ThiefsReflexes, Elusive, EpicBoon)
)

class ThiefRogue20 : Rogue(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(SneakAttack(damageDice = 10), Expertise(skills = 4), ThievesCant, CunningAction, SteadyAim, FastHands, SecondStoryWork, UncannyDodge, Evasion, SupremeSneak, ReliableTalent, UseMagicDevice, SlipperyMind, ThiefsReflexes, Elusive, EpicBoon, StrokeOfLuck)
)
