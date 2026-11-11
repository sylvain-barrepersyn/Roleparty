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

object JournalEntryTable : Table("$SCHEMA_CAMPAIGN.journal_entry") {
    val id = journalEntryId("id")
    val playerCharacterId = playerCharacterId("player_character_id")
    val title = text("title")
    val text = text("text")

    override val primaryKey = PrimaryKey(id)
}

object JournalEntryImageTable : Table("$SCHEMA_CAMPAIGN.journal_entry_image") {
    val journalEntryId = journalEntryId("journal_entry_id")
    val position = integer("position")
    val imageId = imageId("image_id")

    override val primaryKey = PrimaryKey(journalEntryId, position)
}
