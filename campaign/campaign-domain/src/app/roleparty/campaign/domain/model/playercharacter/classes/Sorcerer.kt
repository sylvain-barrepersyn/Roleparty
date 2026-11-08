package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Sorcerer(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 6,
    level = level,
    features = features,
    armorTrainings = emptySet(),
    savingThrows = setOf(SavingThrowProficiency.ConstitutionSave, SavingThrowProficiency.CharismaSave),
    proficiencyBonus = proficiencyBonus
) {
    data class SorceryPoints(val count: Int) : Feature
    data class MetaMagic(val options: Int) : Feature
    data object Spellcasting : Feature
    data object InnateSorcery : Feature
    data object FontOfMagic : Feature
    data object SorcerousRestoration : Feature
    data object EpicBoon : Feature
    data object WildMagicSurge : Feature
    data object TidesOfChaos : Feature
    data object BendLuck : Feature
    data object ControlledChaos : Feature
    data object SpellBombardment : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 10,
            dexterity = 13,
            constitution = 14,
            intelligence = 8,
            wisdom = 12,
            charisma = 15
        )
    }
}

class Sorcerer1 : Sorcerer(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(1))
)

class Sorcerer2 : Sorcerer(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(2), SorceryPoints(count = 2), FontOfMagic)
)

class WildMagicSorcerer3 : Sorcerer(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(3), SorceryPoints(count = 3), FontOfMagic, MetaMagic(options = 2), WildMagicSurge, TidesOfChaos)
)

class WildMagicSorcerer4 : Sorcerer(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(4), SorceryPoints(count = 4), FontOfMagic, MetaMagic(options = 2), WildMagicSurge, TidesOfChaos)
)

class WildMagicSorcerer5 : Sorcerer(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(5), SorceryPoints(count = 5), FontOfMagic, MetaMagic(options = 2), WildMagicSurge, TidesOfChaos)
)

class WildMagicSorcerer6 : Sorcerer(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(6), SorceryPoints(count = 6), FontOfMagic, MetaMagic(options = 2), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer7 : Sorcerer(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(7), SorceryPoints(count = 7), FontOfMagic, MetaMagic(options = 3), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer8 : Sorcerer(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(8), SorceryPoints(count = 8), FontOfMagic, MetaMagic(options = 3), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer9 : Sorcerer(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(9), SorceryPoints(count = 9), FontOfMagic, MetaMagic(options = 3), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer10 : Sorcerer(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(10), SorceryPoints(count = 10), FontOfMagic, MetaMagic(options = 4), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer11 : Sorcerer(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(11), SorceryPoints(count = 11), FontOfMagic, MetaMagic(options = 4), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer12 : Sorcerer(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(12), SorceryPoints(count = 12), FontOfMagic, MetaMagic(options = 4), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer13 : Sorcerer(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(13), SorceryPoints(count = 13), FontOfMagic, MetaMagic(options = 4), WildMagicSurge, TidesOfChaos, BendLuck)
)

class WildMagicSorcerer14 : Sorcerer(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(14), SorceryPoints(count = 14), FontOfMagic, MetaMagic(options = 4), WildMagicSurge, TidesOfChaos, BendLuck, ControlledChaos)
)

class WildMagicSorcerer15 : Sorcerer(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(15), SorceryPoints(count = 15), FontOfMagic, MetaMagic(options = 5), WildMagicSurge, TidesOfChaos, BendLuck, ControlledChaos)
)

class WildMagicSorcerer16 : Sorcerer(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(16), SorceryPoints(count = 16), FontOfMagic, MetaMagic(options = 5), WildMagicSurge, TidesOfChaos, BendLuck, ControlledChaos)
)

class WildMagicSorcerer17 : Sorcerer(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(17), SorceryPoints(count = 17), FontOfMagic, MetaMagic(options = 5), WildMagicSurge, TidesOfChaos, BendLuck, ControlledChaos)
)

class WildMagicSorcerer18 : Sorcerer(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(18), SorceryPoints(count = 18), FontOfMagic, MetaMagic(options = 5), WildMagicSurge, TidesOfChaos, BendLuck, ControlledChaos, SpellBombardment)
)

class WildMagicSorcerer19 : Sorcerer(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(19), SorceryPoints(count = 19), FontOfMagic, MetaMagic(options = 5), WildMagicSurge, TidesOfChaos, BendLuck, ControlledChaos, SpellBombardment, EpicBoon)
)

class WildMagicSorcerer20 : Sorcerer(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(Spellcasting, InnateSorcery, fullCasterSlots(20), SorceryPoints(count = 20), FontOfMagic, MetaMagic(options = 6), WildMagicSurge, TidesOfChaos, BendLuck, ControlledChaos, SpellBombardment, EpicBoon, SorcerousRestoration)
)
