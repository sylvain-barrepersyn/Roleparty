package app.roleparty.campaign.creature.domain.model.playercharacter

data class AbilityScores(
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
) {

    operator fun get(ability: Ability): Int = when (ability) {
        Ability.STRENGTH -> strength
        Ability.DEXTERITY -> dexterity
        Ability.CONSTITUTION -> constitution
        Ability.INTELLIGENCE -> intelligence
        Ability.WISDOM -> wisdom
        Ability.CHARISMA -> charisma
    }

    fun modifier(ability: Ability): Int = Math.floorDiv(this[ability] - 10, 2)
}
