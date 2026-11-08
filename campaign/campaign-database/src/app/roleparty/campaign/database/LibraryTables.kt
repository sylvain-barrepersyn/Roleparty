package app.roleparty.campaign.database

import org.jetbrains.exposed.v1.core.Table

object LibraryEntryTable : Table("$SCHEMA_CAMPAIGN.library_entry") {
    val id = libraryEntryId("id")
    val campaignId = campaignId("campaign_id")
    val title = text("title")
    val description = text("description")
    val imageId = imageId("image_id")

    override val primaryKey = PrimaryKey(id)
}
