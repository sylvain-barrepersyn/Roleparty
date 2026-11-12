package app.roleparty.campaign.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

object TabletopTable : Table("$SCHEMA_CAMPAIGN.tabletop") {
    val campaignId = campaignId("campaign_id")
    val gameSessionId = gameSessionId("game_session_id")
    val startedAt = timestamp("started_at")

    override val primaryKey = PrimaryKey(campaignId)
}
