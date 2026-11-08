package app.roleparty.campaign.domain.model.playercharacter

sealed interface Language

enum class StandardLanguage : Language {
    COMMON,
    COMMON_SIGN_LANGUAGE,
    DRACONIC,
    DWARVISH,
    ELVISH,
    GIANT,
    GNOMISH,
    GOBLIN,
    HALFLING,
    ORC,
}

enum class RareLanguage : Language {
    ABYSSAL,
    CELESTIAL,
    DEEP_SPEECH,
    DRUIDIC,
    INFERNAL,
    PRIMORDIAL,
    SYLVAN,
    THIEVES_CANT,
    UNDERCOMMON,
}