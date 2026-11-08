package app.roleparty.campaign.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.date

object GameSessionTable : Table("$SCHEMA_CAMPAIGN.game_session") {
    val id = gameSessionId("id")
    val campaignId = campaignId("campaign_id")
    val date = date("date")
    val name = text("name").nullable()

    override val primaryKey = PrimaryKey(id)
}
