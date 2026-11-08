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

object PlayerCharacterTable : Table("$SCHEMA_CAMPAIGN.player_character") {
    val id = playerCharacterId("id")
    val campaignId = campaignId("campaign_id")
    val player = accountId("player")
    val name = text("name")
    val imageId = imageId("image_id")
    val alignment = text("alignment")
    val strength = integer("strength")
    val dexterity = integer("dexterity")
    val constitution = integer("constitution")
    val intelligence = integer("intelligence")
    val wisdom = integer("wisdom")
    val charisma = integer("charisma")
    val species = text("species")
    val speciesSize = text("species_size").nullable()
    val background = text("background")
    val backgroundTool = text("background_tool").nullable()
    val appearance = text("appearance")
    val backstory = text("backstory")
    val skillProficiencies = text("skill_proficiencies")
    val languages = text("languages")

    override val primaryKey = PrimaryKey(id)
}

object PlayerCharacterClassTable : Table("$SCHEMA_CAMPAIGN.player_character_class") {
    val playerCharacterId = playerCharacterId("player_character_id")
    val characterClass = text("character_class")
    val level = integer("level")
    val position = integer("position")

    override val primaryKey = PrimaryKey(playerCharacterId, characterClass)
}
