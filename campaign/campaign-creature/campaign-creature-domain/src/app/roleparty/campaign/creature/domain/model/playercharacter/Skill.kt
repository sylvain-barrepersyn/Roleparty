package app.roleparty.campaign.creature.domain.model.playercharacter

enum class Skill(val ability: Ability) {
    ACROBATICS(Ability.DEXTERITY),
    ANIMAL_HANDLING(Ability.WISDOM),
    ARCANA(Ability.INTELLIGENCE),
    ATHLETICS(Ability.STRENGTH),
    DECEPTION(Ability.CHARISMA),
    HISTORY(Ability.INTELLIGENCE),
    INSIGHT(Ability.WISDOM),
    INTIMIDATION(Ability.CHARISMA),
    INVESTIGATION(Ability.INTELLIGENCE),
    MEDICINE(Ability.WISDOM),
    NATURE(Ability.INTELLIGENCE),
    PERCEPTION(Ability.WISDOM),
    PERFORMANCE(Ability.CHARISMA),
    PERSUASION(Ability.CHARISMA),
    RELIGION(Ability.INTELLIGENCE),
    SLEIGHT_OF_HAND(Ability.DEXTERITY),
    STEALTH(Ability.DEXTERITY),
    SURVIVAL(Ability.WISDOM),
}
