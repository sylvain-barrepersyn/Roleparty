package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Monk(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 8,
    level = level,
    features = features,
    armorTrainings = emptySet(),
    savingThrows = setOf(SavingThrowProficiency.StrengthSave, SavingThrowProficiency.DexteritySave),
    proficiencyBonus = proficiencyBonus
) {
    data class FocusPoints(val count: Int) : Feature
    data class MartialArtsDie(val sides: Int) : Feature
    data class UnarmoredMovement(val bonusFeet: Int) : Feature
    data object UnarmoredDefense : Feature
    data object PatientDefense : Feature
    data object StepOfTheWind : Feature
    data object StunningStrike : Feature
    data object SlowFall : Feature
    data object EmpoweredStrikes : Feature
    data object AcrobaticMovement : Feature
    data object SelfRestoration : Feature
    data object PerfectFocus : Feature
    data object DisciplinedSurvivor : Feature
    data object SuperiorDefense : Feature
    data object BodyAndMind : Feature
    data object EpicBoon : Feature
    data object OpenHandTechnique : Feature
    data object WholenessOfBody : Feature
    data object FlightOfCranes : Feature
    data object QuiveringPalm : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 12,
            dexterity = 15,
            constitution = 13,
            intelligence = 10,
            wisdom = 14,
            charisma = 8
        )
    }
}

class Monk1 : Monk(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 6), UnarmoredMovement(bonusFeet = 10))
)

class Monk2 : Monk(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 6), UnarmoredMovement(bonusFeet = 10), FocusPoints(count = 2), PatientDefense, StepOfTheWind, StunningStrike)
)

class OpenHandMonk3 : Monk(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 6), UnarmoredMovement(bonusFeet = 10), FocusPoints(count = 3), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique)
)

class OpenHandMonk4 : Monk(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 6), UnarmoredMovement(bonusFeet = 10), FocusPoints(count = 4), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall)
)

class OpenHandMonk5 : Monk(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 8), UnarmoredMovement(bonusFeet = 10), FocusPoints(count = 5), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2))
)

class OpenHandMonk6 : Monk(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 8), UnarmoredMovement(bonusFeet = 15), FocusPoints(count = 6), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes)
)

class OpenHandMonk7 : Monk(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 8), UnarmoredMovement(bonusFeet = 15), FocusPoints(count = 7), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes)
)

class OpenHandMonk8 : Monk(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 8), UnarmoredMovement(bonusFeet = 15), FocusPoints(count = 8), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes)
)

class OpenHandMonk9 : Monk(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 8), UnarmoredMovement(bonusFeet = 15), FocusPoints(count = 9), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement)
)

class OpenHandMonk10 : Monk(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 8), UnarmoredMovement(bonusFeet = 20), FocusPoints(count = 10), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration)
)

class OpenHandMonk11 : Monk(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 10), UnarmoredMovement(bonusFeet = 20), FocusPoints(count = 11), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody)
)

class OpenHandMonk12 : Monk(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 10), UnarmoredMovement(bonusFeet = 20), FocusPoints(count = 12), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody)
)

class OpenHandMonk13 : Monk(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 10), UnarmoredMovement(bonusFeet = 20), FocusPoints(count = 13), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody)
)

class OpenHandMonk14 : Monk(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 10), UnarmoredMovement(bonusFeet = 25), FocusPoints(count = 14), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody, DisciplinedSurvivor)
)

class OpenHandMonk15 : Monk(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 10), UnarmoredMovement(bonusFeet = 25), FocusPoints(count = 15), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody, DisciplinedSurvivor, PerfectFocus)
)

class OpenHandMonk16 : Monk(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 10), UnarmoredMovement(bonusFeet = 25), FocusPoints(count = 16), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody, DisciplinedSurvivor, PerfectFocus)
)

class OpenHandMonk17 : Monk(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 12), UnarmoredMovement(bonusFeet = 25), FocusPoints(count = 17), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody, DisciplinedSurvivor, PerfectFocus, FlightOfCranes)
)

class OpenHandMonk18 : Monk(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 12), UnarmoredMovement(bonusFeet = 30), FocusPoints(count = 18), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody, DisciplinedSurvivor, PerfectFocus, FlightOfCranes, SuperiorDefense)
)

class OpenHandMonk19 : Monk(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 12), UnarmoredMovement(bonusFeet = 30), FocusPoints(count = 19), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody, DisciplinedSurvivor, PerfectFocus, FlightOfCranes, SuperiorDefense, EpicBoon)
)

class OpenHandMonk20 : Monk(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(UnarmoredDefense, MartialArtsDie(sides = 12), UnarmoredMovement(bonusFeet = 30), FocusPoints(count = 20), PatientDefense, StepOfTheWind, StunningStrike, OpenHandTechnique, SlowFall, ExtraAttack(numberOfAttacks = 2), EmpoweredStrikes, AcrobaticMovement, SelfRestoration, WholenessOfBody, DisciplinedSurvivor, PerfectFocus, FlightOfCranes, SuperiorDefense, EpicBoon, BodyAndMind)
)
