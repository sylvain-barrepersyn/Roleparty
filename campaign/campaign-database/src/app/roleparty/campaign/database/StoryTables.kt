package app.roleparty.campaign.database

import org.jetbrains.exposed.v1.core.Table


object SceneTable : Table("$SCHEMA_CAMPAIGN.scene") {
    val id = sceneId("id")
    val campaignId = campaignId("campaign_id")
    val name = text("name")
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}

object SceneLibraryEntryTable : Table("$SCHEMA_CAMPAIGN.scene_library_entry") {
    val sceneId = sceneId("scene_id")
    val libraryEntryId = libraryEntryId("library_entry_id")
    val position = long("position")

    override val primaryKey = PrimaryKey(sceneId, libraryEntryId)
}
