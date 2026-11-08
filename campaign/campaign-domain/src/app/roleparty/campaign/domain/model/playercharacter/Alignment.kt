package app.roleparty.campaign.domain.model.playercharacter

@ConsistentCopyVisibility
data class Alignment private constructor(
    val ethics: Ethics,
    val morality: Morality
) {
    companion object {
        val LAWFUL_GOOD = Alignment(Ethics.LAWFUL, Morality.GOOD)
        val LAWFUL_NEUTRAL = Alignment(Ethics.LAWFUL, Morality.NEUTRAL)
        val LAWFUL_EVIL = Alignment(Ethics.LAWFUL, Morality.EVIL)

        val CHAOTIC_GOOD = Alignment(Ethics.CHAOTIC, Morality.GOOD)
        val CHAOTIC_NEUTRAL = Alignment(Ethics.CHAOTIC, Morality.NEUTRAL)
        val CHAOTIC_EVIL = Alignment(Ethics.CHAOTIC, Morality.EVIL)

        val NEUTRAL_GOOD = Alignment(Ethics.NEUTRAL, Morality.GOOD)
        val NEUTRAL = Alignment(Ethics.NEUTRAL, Morality.NEUTRAL)
        val NEUTRAL_EVIL = Alignment(Ethics.NEUTRAL, Morality.EVIL)

        fun of(ethics: Ethics, morality: Morality): Alignment {
            return when (ethics) {
                Ethics.LAWFUL -> when (morality) {
                    Morality.GOOD -> LAWFUL_GOOD
                    Morality.NEUTRAL -> LAWFUL_NEUTRAL
                    Morality.EVIL -> LAWFUL_EVIL
                }
                Ethics.NEUTRAL -> when (morality) {
                    Morality.GOOD -> NEUTRAL_GOOD
                    Morality.NEUTRAL -> NEUTRAL
                    Morality.EVIL -> NEUTRAL_EVIL
                }
                Ethics.CHAOTIC -> when (morality) {
                    Morality.GOOD -> CHAOTIC_GOOD
                    Morality.NEUTRAL -> CHAOTIC_NEUTRAL
                    Morality.EVIL -> CHAOTIC_EVIL
                }
            }
        }
    }
}