package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.domain.model.playercharacter.Alignment
import app.roleparty.campaign.domain.model.playercharacter.ArtisanToolProficiency
import app.roleparty.campaign.domain.model.playercharacter.Background
import app.roleparty.campaign.domain.model.playercharacter.GamingSetToolProficiency
import app.roleparty.campaign.domain.model.playercharacter.Language
import app.roleparty.campaign.domain.model.playercharacter.MusicalInstrumentToolProficiency
import app.roleparty.campaign.domain.model.playercharacter.RareLanguage
import app.roleparty.campaign.domain.model.playercharacter.Skill
import app.roleparty.campaign.domain.model.playercharacter.SkillProficiency
import app.roleparty.campaign.domain.model.playercharacter.Species
import app.roleparty.campaign.domain.model.playercharacter.StandardLanguage
import app.roleparty.campaign.domain.model.playercharacter.ToolProficiency
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.domain.model.playercharacter.ArtisanToolProficiency.*
import app.roleparty.campaign.domain.model.playercharacter.Background.*
import app.roleparty.campaign.domain.model.playercharacter.GamingSetToolProficiency.*
import app.roleparty.campaign.domain.model.playercharacter.MusicalInstrumentToolProficiency.*
import app.roleparty.campaign.domain.model.playercharacter.Species.*

internal object PlayerCharacterCodes {

    private val ARTISAN_TOOLS = listOf(
        AlchemistsSupplies, BrewersSupplies, CalligraphersSupplies, CarpentersTools, CartographersTools, CobblersTools,
        CooksUtensils, GlassblowersTools, JewelersTools, LeatherworkersTools, MasonsTools, PaintersSupplies, PottersTools,
        SmithsTools, TinkersTools, WeaversTools, WoodcarversTools,
    )
    private val GAMING_SETS = listOf(DiceSet, DragonchessSet, PlayingCardSet, ThreeDragonAnteSet)
    private val MUSICAL_INSTRUMENTS = listOf(Bagpipes, Drum, Dulcimer, Flute, Horn, Lute, Lyre, PanFlute, Shawm, Viol)

    private val ALIGNMENTS = with(Alignment) {
        mapOf(
            "LAWFUL_GOOD" to LAWFUL_GOOD, "NEUTRAL_GOOD" to NEUTRAL_GOOD, "CHAOTIC_GOOD" to CHAOTIC_GOOD,
            "LAWFUL_NEUTRAL" to LAWFUL_NEUTRAL, "NEUTRAL" to NEUTRAL, "CHAOTIC_NEUTRAL" to CHAOTIC_NEUTRAL,
            "LAWFUL_EVIL" to LAWFUL_EVIL, "NEUTRAL_EVIL" to NEUTRAL_EVIL, "CHAOTIC_EVIL" to CHAOTIC_EVIL,
        )
    }

    private val SIMPLE_SPECIES = mapOf("DRAGONBORN" to Dragonborn, "DWARF" to Dwarf, "ELF" to Elf, "GNOME" to Gnome, "HALFLING" to Halfling, "ORC" to Orc, "TIEFLING" to Tiefling)

    private val SIMPLE_BACKGROUNDS = listOf(Acolyte, Charlatan, Criminal, Farmer, Guide, Hermit, Merchant, Sage, Sailor, Scribe, Wayfarer).associateBy { it.toString().uppercase() }

    fun code(characterClass: CharacterClass) = characterClass.name

    fun characterClass(code: String) = CharacterClass.entries.firstOrNull { it.name == code }

    fun code(alignment: Alignment) = ALIGNMENTS.entries.first { it.value == alignment }.key

    fun alignment(code: String) = ALIGNMENTS[code]

    fun code(skill: SkillProficiency) = skill.skill.name

    fun skill(code: String) = Skill.entries.firstOrNull { it.name == code }?.let(SkillProficiency::fromSkill)

    fun code(language: Language) = when (language) {
        is StandardLanguage -> language.name
        is RareLanguage -> language.name
    }

    fun language(code: String): Language? =
        StandardLanguage.entries.firstOrNull { it.name == code } ?: RareLanguage.entries.firstOrNull { it.name == code }

    fun code(species: Species) = when (species) {
        is Species.Human -> "HUMAN"
        is Species.Goliath -> "GOLIATH"
        else -> SIMPLE_SPECIES.entries.first { it.value == species }.key
    }

    fun sizeCode(species: Species) = when (species) {
        is Species.Human -> species.chosenSize.name
        is Species.Goliath -> species.chosenSize.name
        else -> null
    }

    fun species(code: String, size: String?): Species? = when (code) {
        "HUMAN" -> Species.Human(size?.let { s -> Species.Human.Size.entries.firstOrNull { it.name == s } ?: return null } ?: Species.Human.Size.MEDIUM)
        "GOLIATH" -> Species.Goliath(size?.let { s -> Species.Goliath.Size.entries.firstOrNull { it.name == s } ?: return null } ?: Species.Goliath.Size.MEDIUM)
        else -> SIMPLE_SPECIES[code]
    }

    fun code(background: Background) = when (background) {
        is Background.Artisan -> "ARTISAN"
        is Background.Entertainer -> "ENTERTAINER"
        is Background.Guard -> "GUARD"
        is Background.Noble -> "NOBLE"
        is Background.Soldier -> "SOLDIER"
        else -> SIMPLE_BACKGROUNDS.entries.first { it.value == background }.key
    }

    fun toolCode(background: Background) = when (background) {
        is Background.Artisan -> code(background.toolProficiency)
        is Background.Entertainer -> code(background.toolProficiency)
        is Background.Guard -> code(background.toolProficiency)
        is Background.Noble -> code(background.toolProficiency)
        is Background.Soldier -> code(background.toolProficiency)
        else -> null
    }

    fun background(code: String, tool: String?): Background? = when (code) {
        "ARTISAN" -> ARTISAN_TOOLS.find(tool)?.let(Background::Artisan)
        "ENTERTAINER" -> MUSICAL_INSTRUMENTS.find(tool)?.let(Background::Entertainer)
        "GUARD" -> GAMING_SETS.find(tool)?.let(Background::Guard)
        "NOBLE" -> GAMING_SETS.find(tool)?.let(Background::Noble)
        "SOLDIER" -> GAMING_SETS.find(tool)?.let(Background::Soldier)
        else -> SIMPLE_BACKGROUNDS[code]
    }

    private fun code(tool: ToolProficiency) = tool.toString().replace(Regex("(?<=[a-z0-9])(?=[A-Z])"), "_").uppercase()

    private fun <T : ToolProficiency> List<T>.find(code: String?) = firstOrNull { code(it) == code }
}
