package app.roleparty.campaign.database

import org.jetbrains.exposed.v1.core.Table

object CampaignTable : Table("$SCHEMA_CAMPAIGN.campaign") {
    val id = campaignId("id")
    val name = text("name")
    val description = text("description")
    val imageId = imageId("image_id")
    val gameMaster = accountId("game_master")

    override val primaryKey = PrimaryKey(id)
}

object PlayerTable : Table("$SCHEMA_CAMPAIGN.player") {
    val campaignId = campaignId("campaign_id")
    val accountId = accountId("account_id")

    override val primaryKey = PrimaryKey(campaignId, accountId)
}

object InvitationTable : Table("$SCHEMA_CAMPAIGN.invitation") {
    val code = text("code")
    val campaignId = campaignId("campaign_id")
    val used = bool("used")

    override val primaryKey = PrimaryKey(code)
}
