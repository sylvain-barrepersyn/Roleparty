package app.roleparty.campaign.library.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.database.CampaignTable
import app.roleparty.campaign.database.PlayerCharacterContent
import app.roleparty.campaign.database.PlayerCharacterTable
import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.core.database.connectPostgres
import app.roleparty.media.commons.ImageId
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.testcontainers.postgresql.PostgreSQLContainer

internal object TestPostgres {
    private val container = PostgreSQLContainer("postgres:18").apply { start() }

    val postgres = connectPostgres(
        mapOf(
            "POSTGRES_HOST" to container.host,
            "POSTGRES_PORT" to container.firstMappedPort.toString(),
            "POSTGRES_USER" to container.username,
            "POSTGRES_PASSWORD" to container.password,
            "POSTGRES_DATABASE_NAME" to container.databaseName,
        )
    ).apply {
        setUpCampaignSchema()
    }

    val database get() = postgres.database

    suspend fun givenCampaign(gameMaster: AccountId = AccountId.generate()): CampaignId {
        val campaignId = CampaignId.generate()
        suspendTransaction(database) {
            CampaignTable.insert {
                it[id] = campaignId
                it[name] = "The Lost Mine"
                it[description] = ""
                it[imageId] = ImageId.generate()
                it[CampaignTable.gameMaster] = gameMaster
            }
        }
        return campaignId
    }

    suspend fun givenPlayerCharacter(player: AccountId = AccountId.generate()): PlayerCharacterId {
        val campaignId = givenCampaign()
        val playerCharacterId = PlayerCharacterId.generate()
        suspendTransaction(database) {
            PlayerCharacterTable.insert {
                it[id] = playerCharacterId
                it[PlayerCharacterTable.campaignId] = campaignId
                it[PlayerCharacterTable.player] = player
                it[name] = "Gimli"
                it[imageId] = ImageId.generate()
                it[content] = PlayerCharacterContent(
                    classes = emptyList(),
                    classFeatures = "",
                    savingThrows = emptySet(),
                    armorTrainings = emptySet(),
                    background = PlayerCharacterContent.BackgroundContent("", "", ""),
                    skillProficiencies = emptySet(),
                    species = PlayerCharacterContent.SpeciesContent("", "MEDIUM", 30, ""),
                    languages = emptyList(),
                    equipment = "",
                    coins = 0,
                    abilityScores = emptyMap(),
                    alignment = "NEUTRAL",
                    appearance = "",
                    personality = "",
                    backstory = "",
                    otherDetails = "",
                )
            }
        }
        return playerCharacterId
    }
}
