package app.roleparty.campaign.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.date

object GameSessionTable : Table("$SCHEMA_CAMPAIGN.game_session") {
    val id = gameSessionId("id")
    val campaignId = campaignId("campaign_id")
    val date = date("date")
    val name = text("name").nullable()
    val digitalGamingTable = bool("digital_gaming_table")

    override val primaryKey = PrimaryKey(id)
}

object GameSessionPresenceTable : Table("$SCHEMA_CAMPAIGN.game_session_presence") {
    val gameSessionId = gameSessionId("game_session_id")
    val accountId = accountId("account_id")
    val presence = text("presence")

    override val primaryKey = PrimaryKey(gameSessionId, accountId)
}

object AgendaPreferenceTable : Table("$SCHEMA_CAMPAIGN.agenda_preference") {
    val campaignId = campaignId("campaign_id")
    val accountId = accountId("account_id")
    val presence = text("presence")
    val digitalGamingTable = bool("digital_gaming_table")

    override val primaryKey = PrimaryKey(campaignId, accountId)
}
