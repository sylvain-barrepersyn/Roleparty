package app.roleparty.campaign.database

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.jsonb

@Serializable
data class MonsterSheetContent(
    val armorClass: Int,
    val armorClassDetail: String?,
    val hitPointMax: Int,
    val hitDice: String,
    val speeds: Map<String, Int>,
    val abilities: List<Int>,
    val savingThrows: List<Int> = List(6) { 0 },
    val skills: Map<String, Int> = emptyMap(),
    val passivePerception: Int,
    val languages: List<String>,
    val telepathyFeet: Int?,
    val traits: List<FeatureContent>,
    val actions: List<FeatureContent>,
    val bonusActions: List<FeatureContent>,
    val reactions: List<FeatureContent>,
    val legendaryDescription: String,
    val legendaryActions: List<LegendaryActionContent>,
    val legendaryUses: Int = 3,
    val legendaryUsesInLair: Int? = null,
    val alternativeSize: String? = null,
    val swarmOf: String? = null,
    val typeTags: String = "",
    val alignment: String = "UNALIGNED",
    val initiativeProficiency: String = "NONE",
    val hover: Boolean = false,
    val senses: Map<String, Int> = emptyMap(),
    val vulnerabilities: List<String> = emptyList(),
    val resistances: List<String> = emptyList(),
    val immunities: List<String> = emptyList(),
    val conditionImmunities: List<String> = emptyList(),
    val understoodLanguages: List<String> = emptyList(),
    val otherLanguages: String = "",
    val gear: List<String> = emptyList(),
    val lair: Boolean = false,
    val habitats: List<String> = emptyList(),
    val treasures: List<String> = emptyList(),
) {
    @Serializable
    data class FeatureContent(val name: String, val description: String)

    @Serializable
    data class LegendaryActionContent(val name: String, val description: String, val cost: Int)
}

private val json = Json { ignoreUnknownKeys = true }

object MonsterSheetTable : Table("$SCHEMA_CAMPAIGN.monster_sheet") {
    val id = monsterSheetId("id")
    val campaignId = campaignId("campaign_id")
    val name = text("name")
    val imageId = imageId("image_id").nullable()
    val size = text("size")
    val type = text("type")
    val challengeRating = text("challenge_rating")
    val content = jsonb<MonsterSheetContent>("content", json)

    override val primaryKey = PrimaryKey(id)
}

object CharacterSheetTable : Table("$SCHEMA_CAMPAIGN.character_sheet") {
    val id = characterSheetId("id")
    val campaignId = campaignId("campaign_id")
    val name = text("name")
    val imageId = imageId("image_id").nullable()
    val notes = text("notes")
    val monsterSheetId = monsterSheetId("monster_sheet_id").nullable()
    val size = text("size").nullable()
    val type = text("type").nullable()
    val challengeRating = text("challenge_rating").nullable()
    val content = jsonb<MonsterSheetContent>("content", json).nullable()

    override val primaryKey = PrimaryKey(id)
}

object PlayerCharacterTable : Table("$SCHEMA_CAMPAIGN.player_character") {
    val id = playerCharacterId("id")
    val campaignId = campaignId("campaign_id")
    val player = accountId("player")
    val name = text("name")
    val imageId = imageId("image_id")
    val content = jsonb<PlayerCharacterContent>("content", playerCharacterJson)

    override val primaryKey = PrimaryKey(id)
}

private val playerCharacterJson = Json { ignoreUnknownKeys = true }

@Serializable
data class PlayerCharacterContent(
    val classes: List<ClassContent>,
    val classFeatures: String,
    val savingThrows: Set<String>,
    val armorTrainings: Set<String>,
    val background: BackgroundContent,
    val skillProficiencies: Set<String>,
    val species: SpeciesContent,
    val languages: List<String>,
    val equipment: String,
    val coins: Int,
    val abilityScores: Map<String, Int>,
    val alignment: String,
    val appearance: String,
    val personality: String,
    val backstory: String,
    val otherDetails: String,
) {

    @Serializable
    data class ClassContent(val name: String, val level: Int, val hitDie: String)

    @Serializable
    data class BackgroundContent(val name: String, val originFeat: String, val toolProficiency: String)

    @Serializable
    data class SpeciesContent(val name: String, val size: String, val speed: Int, val traits: String)
}
