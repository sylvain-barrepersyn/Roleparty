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
