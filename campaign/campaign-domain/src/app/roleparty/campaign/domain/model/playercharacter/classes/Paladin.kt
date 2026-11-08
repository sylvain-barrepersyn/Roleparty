package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Paladin(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 10,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT, ArmorCategory.MEDIUM, ArmorCategory.HEAVY, ArmorCategory.SHIELD),
    savingThrows = setOf(SavingThrowProficiency.WisdomSave, SavingThrowProficiency.CharismaSave),
    proficiencyBonus = proficiencyBonus
) {
    data class LayOnHands(val pool: Int) : Feature
    data object DivineSmite : Feature
    data object LayingOnHands : Feature
    data object AuraOfProtection : Feature
    data object AuraOfCourage : Feature
    data object DivineSense : Feature
    data object RestorativeTouch : Feature
    data object AuraExpansion : Feature
    data object EpicBoon : Feature
    data object SacredWeapon : Feature
    data object AuraOfDevotion : Feature
    data object SmiteOfGoodness : Feature
    data object HolyNimbus : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 15,
            dexterity = 10,
            constitution = 13,
            intelligence = 8,
            wisdom = 12,
            charisma = 14
        )
    }
}

class Paladin1 : Paladin(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(DivineSense, LayOnHands(pool = 5), DivineSmite)
)

class Paladin2 : Paladin(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(DivineSense, LayOnHands(pool = 10), DivineSmite, halfCasterSlots(2))
)

class DevotionPaladin3 : Paladin(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(DivineSense, LayOnHands(pool = 15), DivineSmite, halfCasterSlots(3), SacredWeapon)
)

class DevotionPaladin4 : Paladin(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(DivineSense, LayOnHands(pool = 20), DivineSmite, halfCasterSlots(4), SacredWeapon)
)

class DevotionPaladin5 : Paladin(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(DivineSense, LayOnHands(pool = 25), DivineSmite, halfCasterSlots(5), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch)
)

class DevotionPaladin6 : Paladin(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(DivineSense, LayOnHands(pool = 30), DivineSmite, halfCasterSlots(6), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection)
)

class DevotionPaladin7 : Paladin(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(DivineSense, LayOnHands(pool = 35), DivineSmite, halfCasterSlots(7), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion)
)

class DevotionPaladin8 : Paladin(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(DivineSense, LayOnHands(pool = 40), DivineSmite, halfCasterSlots(8), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion)
)

class DevotionPaladin9 : Paladin(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(DivineSense, LayOnHands(pool = 45), DivineSmite, halfCasterSlots(9), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion)
)

class DevotionPaladin10 : Paladin(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(DivineSense, LayOnHands(pool = 50), DivineSmite, halfCasterSlots(10), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage)
)

class DevotionPaladin11 : Paladin(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(DivineSense, LayOnHands(pool = 55), DivineSmite, halfCasterSlots(11), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage)
)

class DevotionPaladin12 : Paladin(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(DivineSense, LayOnHands(pool = 60), DivineSmite, halfCasterSlots(12), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage)
)

class DevotionPaladin13 : Paladin(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(DivineSense, LayOnHands(pool = 65), DivineSmite, halfCasterSlots(13), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage)
)

class DevotionPaladin14 : Paladin(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(DivineSense, LayOnHands(pool = 70), DivineSmite, halfCasterSlots(14), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage, AuraExpansion)
)

class DevotionPaladin15 : Paladin(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(DivineSense, LayOnHands(pool = 75), DivineSmite, halfCasterSlots(15), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage, AuraExpansion)
)

class DevotionPaladin16 : Paladin(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(DivineSense, LayOnHands(pool = 80), DivineSmite, halfCasterSlots(16), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage, AuraExpansion)
)

class DevotionPaladin17 : Paladin(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(DivineSense, LayOnHands(pool = 85), DivineSmite, halfCasterSlots(17), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage, AuraExpansion)
)

class DevotionPaladin18 : Paladin(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(DivineSense, LayOnHands(pool = 90), DivineSmite, halfCasterSlots(18), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage, AuraExpansion)
)

class DevotionPaladin19 : Paladin(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(DivineSense, LayOnHands(pool = 95), DivineSmite, halfCasterSlots(19), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage, AuraExpansion, EpicBoon)
)

class DevotionPaladin20 : Paladin(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(DivineSense, LayOnHands(pool = 100), DivineSmite, halfCasterSlots(20), SacredWeapon, ExtraAttack(numberOfAttacks = 2), RestorativeTouch, AuraOfProtection, AuraOfDevotion, AuraOfCourage, AuraExpansion, EpicBoon, SmiteOfGoodness, HolyNimbus)
)
