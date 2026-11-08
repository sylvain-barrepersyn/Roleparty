package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Warlock(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 8,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT),
    savingThrows = setOf(SavingThrowProficiency.WisdomSave, SavingThrowProficiency.CharismaSave),
    proficiencyBonus = proficiencyBonus
) {
    data class PactMagicSlots(val slots: Int, val slotLevel: Int) : Feature
    data class EldritchInvocations(val count: Int) : Feature
    data class MysticArcanum(val spellLevel: Int) : Feature
    data object EldritchBlast : Feature
    data object PactBoon : Feature
    data object MysticArcanum11 : Feature
    data object EldritchMaster : Feature
    data object EpicBoon : Feature
    data object DarkOnesBlessing : Feature
    data object DarkOnesOwnLuck : Feature
    data object FiendishResilience : Feature
    data object HurlThroughHell : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 8,
            dexterity = 14,
            constitution = 13,
            intelligence = 12,
            wisdom = 10,
            charisma = 15
        )
    }
}

class Warlock1 : Warlock(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 1, slotLevel = 1))
)

class Warlock2 : Warlock(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 1), EldritchInvocations(count = 2))
)

class FiendWarlock3 : Warlock(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 2), EldritchInvocations(count = 2), PactBoon, DarkOnesBlessing)
)

class FiendWarlock4 : Warlock(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 2), EldritchInvocations(count = 3), PactBoon, DarkOnesBlessing)
)

class FiendWarlock5 : Warlock(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 3), EldritchInvocations(count = 4), PactBoon, DarkOnesBlessing)
)

class FiendWarlock6 : Warlock(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 3), EldritchInvocations(count = 4), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck)
)

class FiendWarlock7 : Warlock(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 4), EldritchInvocations(count = 5), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck)
)

class FiendWarlock8 : Warlock(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 4), EldritchInvocations(count = 5), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck)
)

class FiendWarlock9 : Warlock(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 5), EldritchInvocations(count = 6), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck)
)

class FiendWarlock10 : Warlock(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 2, slotLevel = 5), EldritchInvocations(count = 6), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience)
)

class FiendWarlock11 : Warlock(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 7), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 6))
)

class FiendWarlock12 : Warlock(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 8), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 6))
)

class FiendWarlock13 : Warlock(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 8), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 7))
)

class FiendWarlock14 : Warlock(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 8), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 7), HurlThroughHell)
)

class FiendWarlock15 : Warlock(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 9), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 8), HurlThroughHell)
)

class FiendWarlock16 : Warlock(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 9), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 8), HurlThroughHell)
)

class FiendWarlock17 : Warlock(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 9), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 9), HurlThroughHell)
)

class FiendWarlock18 : Warlock(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 10), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 9), HurlThroughHell)
)

class FiendWarlock19 : Warlock(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 3, slotLevel = 5), EldritchInvocations(count = 10), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 9), HurlThroughHell, EpicBoon)
)

class FiendWarlock20 : Warlock(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(EldritchBlast, PactMagicSlots(slots = 4, slotLevel = 5), EldritchInvocations(count = 11), PactBoon, DarkOnesBlessing, DarkOnesOwnLuck, FiendishResilience, MysticArcanum(spellLevel = 9), HurlThroughHell, EpicBoon, EldritchMaster)
)
