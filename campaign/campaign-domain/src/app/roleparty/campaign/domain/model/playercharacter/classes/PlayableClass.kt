package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class PlayableClass(
    val hitDice: Int,
    val level: Level,
    val features: List<Feature>,
    val armorTrainings: Set<ArmorCategory>,
    val savingThrows: Set<SavingThrowProficiency>,
    val proficiencyBonus: Int,
) {

    enum class Level : Comparable<Level> {
        LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_4, LEVEL_5, LEVEL_6, LEVEL_7, LEVEL_8, LEVEL_9, LEVEL_10, LEVEL_11, LEVEL_12, LEVEL_13, LEVEL_14, LEVEL_15, LEVEL_16, LEVEL_17, LEVEL_18, LEVEL_19, LEVEL_20;

        fun toInt() = ordinal + 1

        fun next(): Level {
            require(ordinal < entries.lastIndex) { "Cannot level up to the next level" }
            return entries[ordinal + 1]
        }

        companion object {
            fun fromInt(level: Int) = entries[level - 1]
        }
    }

    sealed interface Feature

    override fun equals(other: Any?) = other != null && other::class == this::class

    override fun hashCode() = this::class.hashCode()
}