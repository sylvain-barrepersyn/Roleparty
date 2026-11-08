package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Wizard(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 6,
    level = level,
    features = features,
    armorTrainings = emptySet(),
    savingThrows = setOf(SavingThrowProficiency.IntelligenceSave, SavingThrowProficiency.WisdomSave),
    proficiencyBonus = proficiencyBonus
) {
    data class ArcaneRecovery(val slotsLevel: Int) : Feature
    data object RitualAdept : Feature
    data object ScholarlyMagic : Feature
    data object MemorizeSpell : Feature
    data object SpellMastery : Feature
    data object SignatureSpells : Feature
    data object EpicBoon : Feature
    data object EvokersSculptSpells : Feature
    data object PotentCantrip : Feature
    data object EmpoweredEvocation : Feature
    data object Overchannel : Feature
    data object EvokersSurge : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 8,
            dexterity = 12,
            constitution = 13,
            intelligence = 15,
            wisdom = 14,
            charisma = 10
        )
    }
}

class Wizard1 : Wizard(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 1), fullCasterSlots(1))
)

class Wizard2 : Wizard(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 1), fullCasterSlots(2))
)

class EvocationWizard3 : Wizard(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 2), fullCasterSlots(3), EvokersSculptSpells)
)

class EvocationWizard4 : Wizard(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 2), fullCasterSlots(4), EvokersSculptSpells)
)

class EvocationWizard5 : Wizard(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 3), fullCasterSlots(5), EvokersSculptSpells, MemorizeSpell)
)

class EvocationWizard6 : Wizard(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 3), fullCasterSlots(6), EvokersSculptSpells, MemorizeSpell, PotentCantrip)
)

class EvocationWizard7 : Wizard(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 4), fullCasterSlots(7), EvokersSculptSpells, MemorizeSpell, PotentCantrip)
)

class EvocationWizard8 : Wizard(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 4), fullCasterSlots(8), EvokersSculptSpells, MemorizeSpell, PotentCantrip)
)

class EvocationWizard9 : Wizard(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 5), fullCasterSlots(9), EvokersSculptSpells, MemorizeSpell, PotentCantrip)
)

class EvocationWizard10 : Wizard(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 5), fullCasterSlots(10), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation)
)

class EvocationWizard11 : Wizard(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 6), fullCasterSlots(11), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation)
)

class EvocationWizard12 : Wizard(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 6), fullCasterSlots(12), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation)
)

class EvocationWizard13 : Wizard(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 7), fullCasterSlots(13), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation)
)

class EvocationWizard14 : Wizard(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 7), fullCasterSlots(14), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation, Overchannel)
)

class EvocationWizard15 : Wizard(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 8), fullCasterSlots(15), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation, Overchannel)
)

class EvocationWizard16 : Wizard(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 8), fullCasterSlots(16), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation, Overchannel)
)

class EvocationWizard17 : Wizard(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 9), fullCasterSlots(17), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation, Overchannel)
)

class EvocationWizard18 : Wizard(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 9), fullCasterSlots(18), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation, Overchannel, EvokersSurge, SpellMastery)
)

class EvocationWizard19 : Wizard(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 10), fullCasterSlots(19), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation, Overchannel, EvokersSurge, SpellMastery, EpicBoon)
)

class EvocationWizard20 : Wizard(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(ScholarlyMagic, RitualAdept, ArcaneRecovery(slotsLevel = 10), fullCasterSlots(20), EvokersSculptSpells, MemorizeSpell, PotentCantrip, EmpoweredEvocation, Overchannel, EvokersSurge, SpellMastery, EpicBoon, SignatureSpells)
)
