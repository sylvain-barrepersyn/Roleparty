package app.roleparty.campaign.database

import app.roleparty.core.document.Document
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.jsonb

private val documentJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

object SceneTable : Table("$SCHEMA_CAMPAIGN.scene") {
    val id = sceneId("id")
    val campaignId = campaignId("campaign_id")
    val name = text("name")
    val description = jsonb<Document>("description", documentJson)

    override val primaryKey = PrimaryKey(id)
}

object SceneLibraryEntryTable : Table("$SCHEMA_CAMPAIGN.scene_library_entry") {
    val sceneId = sceneId("scene_id")
    val libraryEntryId = libraryEntryId("library_entry_id")
    val position = long("position")

    override val primaryKey = PrimaryKey(sceneId, libraryEntryId)
}
