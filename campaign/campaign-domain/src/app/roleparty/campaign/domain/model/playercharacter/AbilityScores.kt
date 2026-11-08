package app.roleparty.campaign.domain.model.playercharacter

data class AbilityScores(
    val strength: Strength,
    val dexterity: Dexterity,
    val constitution: Constitution,
    val intelligence: Intelligence,
    val wisdom: Wisdom,
    val charisma: Charisma
) {
    constructor(
        strength: Int,
        dexterity: Int,
        constitution: Int,
        intelligence: Int,
        wisdom: Int,
        charisma: Int
    ) : this(
        strength = Strength(strength),
        dexterity = Dexterity(dexterity),
        constitution = Constitution(constitution),
        intelligence = Intelligence(intelligence),
        wisdom = Wisdom(wisdom),
        charisma = Charisma(charisma)
    )

    companion object {
        val ZERO = AbilityScores(
            strength = Strength(value = 0),
            dexterity = Dexterity(0),
            constitution = Constitution(0),
            intelligence = Intelligence(0),
            wisdom = Wisdom(0),
            charisma = Charisma(0)
        )
    }
}

sealed interface AbilityScore {
    val value: Int
}
data class Strength(override val value: Int) : AbilityScore
data class Dexterity(override val value: Int) : AbilityScore
data class Constitution(override val value: Int) : AbilityScore
data class Intelligence(override val value: Int) : AbilityScore
data class Wisdom(override val value: Int) : AbilityScore
data class Charisma(override val value: Int) : AbilityScore