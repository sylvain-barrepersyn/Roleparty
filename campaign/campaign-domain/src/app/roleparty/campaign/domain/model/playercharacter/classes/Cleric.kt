package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Cleric(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 8,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT, ArmorCategory.MEDIUM, ArmorCategory.SHIELD),
    savingThrows = setOf(SavingThrowProficiency.WisdomSave, SavingThrowProficiency.CharismaSave),
    proficiencyBonus = proficiencyBonus
) {
    data class ChannelDivinity(val uses: Int) : Feature
    data object DivineOrder : Feature
    data object SmiteUndead : Feature
    data object BlessedStrikes : Feature
    data object Commune : Feature
    data object DivineIntervention : Feature
    data object ImprovedDivineIntervention : Feature
    data object EpicBoon : Feature
    data object DiscipleOfLife : Feature
    data object PreserveLife : Feature
    data object BlessedHealer : Feature
    data object SupremeHealing : Feature

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 14,
            dexterity = 8,
            constitution = 12,
            intelligence = 10,
            wisdom = 15,
            charisma = 12
        )
    }
}

class Cleric1 : Cleric(
    level = Level.LEVEL_1, proficiencyBonus = 2,
    features = listOf(DivineOrder, fullCasterSlots(1))
)

class Cleric2 : Cleric(
    level = Level.LEVEL_2, proficiencyBonus = 2,
    features = listOf(DivineOrder, fullCasterSlots(2), ChannelDivinity(uses = 2))
)

class LifeCleric3 : Cleric(
    level = Level.LEVEL_3, proficiencyBonus = 2,
    features = listOf(DivineOrder, fullCasterSlots(3), ChannelDivinity(uses = 2), DiscipleOfLife)
)

class LifeCleric4 : Cleric(
    level = Level.LEVEL_4, proficiencyBonus = 2,
    features = listOf(DivineOrder, fullCasterSlots(4), ChannelDivinity(uses = 2), DiscipleOfLife)
)

class LifeCleric5 : Cleric(
    level = Level.LEVEL_5, proficiencyBonus = 3,
    features = listOf(DivineOrder, fullCasterSlots(5), ChannelDivinity(uses = 2), DiscipleOfLife, SmiteUndead)
)

class LifeCleric6 : Cleric(
    level = Level.LEVEL_6, proficiencyBonus = 3,
    features = listOf(DivineOrder, fullCasterSlots(6), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife)
)

class LifeCleric7 : Cleric(
    level = Level.LEVEL_7, proficiencyBonus = 3,
    features = listOf(DivineOrder, fullCasterSlots(7), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes)
)

class LifeCleric8 : Cleric(
    level = Level.LEVEL_8, proficiencyBonus = 3,
    features = listOf(DivineOrder, fullCasterSlots(8), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes)
)

class LifeCleric9 : Cleric(
    level = Level.LEVEL_9, proficiencyBonus = 4,
    features = listOf(DivineOrder, fullCasterSlots(9), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune)
)

class LifeCleric10 : Cleric(
    level = Level.LEVEL_10, proficiencyBonus = 4,
    features = listOf(DivineOrder, fullCasterSlots(10), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention)
)

class LifeCleric11 : Cleric(
    level = Level.LEVEL_11, proficiencyBonus = 4,
    features = listOf(DivineOrder, fullCasterSlots(11), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention)
)

class LifeCleric12 : Cleric(
    level = Level.LEVEL_12, proficiencyBonus = 4,
    features = listOf(DivineOrder, fullCasterSlots(12), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention)
)

class LifeCleric13 : Cleric(
    level = Level.LEVEL_13, proficiencyBonus = 5,
    features = listOf(DivineOrder, fullCasterSlots(13), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention)
)

class LifeCleric14 : Cleric(
    level = Level.LEVEL_14, proficiencyBonus = 5,
    features = listOf(DivineOrder, fullCasterSlots(14), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention, SupremeHealing)
)

class LifeCleric15 : Cleric(
    level = Level.LEVEL_15, proficiencyBonus = 5,
    features = listOf(DivineOrder, fullCasterSlots(15), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention, SupremeHealing)
)

class LifeCleric16 : Cleric(
    level = Level.LEVEL_16, proficiencyBonus = 5,
    features = listOf(DivineOrder, fullCasterSlots(16), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention, SupremeHealing)
)

class LifeCleric17 : Cleric(
    level = Level.LEVEL_17, proficiencyBonus = 6,
    features = listOf(DivineOrder, fullCasterSlots(17), ChannelDivinity(uses = 3), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention, SupremeHealing)
)

class LifeCleric18 : Cleric(
    level = Level.LEVEL_18, proficiencyBonus = 6,
    features = listOf(DivineOrder, fullCasterSlots(18), ChannelDivinity(uses = -1), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention, SupremeHealing, BlessedHealer)
)

class LifeCleric19 : Cleric(
    level = Level.LEVEL_19, proficiencyBonus = 6,
    features = listOf(DivineOrder, fullCasterSlots(19), ChannelDivinity(uses = -1), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention, SupremeHealing, BlessedHealer, EpicBoon)
)

class LifeCleric20 : Cleric(
    level = Level.LEVEL_20, proficiencyBonus = 6,
    features = listOf(DivineOrder, fullCasterSlots(20), ChannelDivinity(uses = -1), DiscipleOfLife, SmiteUndead, PreserveLife, BlessedStrikes, Commune, DivineIntervention, SupremeHealing, BlessedHealer, EpicBoon, ImprovedDivineIntervention)
)
