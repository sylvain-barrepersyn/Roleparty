package app.roleparty.campaign.tabletop.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.database.CampaignTable
import app.roleparty.campaign.database.GameSessionTable
import app.roleparty.campaign.database.PlayerCharacterContent
import app.roleparty.campaign.database.PlayerCharacterTable
import app.roleparty.campaign.database.PlayerTable
import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.tabletop.domain.model.CampaignParticipants
import app.roleparty.campaign.tabletop.domain.model.GameSession
import app.roleparty.campaign.tabletop.domain.model.PlayerCharacter
import app.roleparty.core.database.connectPostgres
import app.roleparty.media.commons.ImageId
import kotlinx.datetime.LocalDate
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

    suspend fun givenCampaign(participants: CampaignParticipants = CampaignParticipants(AccountId.generate(), emptySet())): CampaignId {
        val campaignId = CampaignId.generate()
        suspendTransaction(database) {
            CampaignTable.insert {
                it[id] = campaignId
                it[name] = "The Lost Mine"
                it[description] = ""
                it[imageId] = ImageId.generate()
                it[gameMaster] = participants.gameMaster
            }
            participants.players.forEach { player ->
                PlayerTable.insert {
                    it[PlayerTable.campaignId] = campaignId
                    it[accountId] = player
                }
            }
        }
        return campaignId
    }

    suspend fun givenGameSession(campaignId: CampaignId, date: LocalDate = LocalDate(2026, 12, 1), name: String? = null): GameSession {
        val gameSession = GameSession(GameSessionId.generate(), campaignId, date, name)
        suspendTransaction(database) {
            GameSessionTable.insert {
                it[id] = gameSession.id
                it[GameSessionTable.campaignId] = campaignId
                it[GameSessionTable.date] = date
                it[GameSessionTable.name] = name
                it[digitalGamingTable] = false
            }
        }
        return gameSession
    }

    suspend fun givenPlayerCharacter(characterName: String): PlayerCharacter {
        val character = PlayerCharacter(PlayerCharacterId.generate(), givenCampaign(), AccountId.generate(), characterName, ImageId.generate())
        suspendTransaction(database) {
            PlayerCharacterTable.insert {
                it[id] = character.id
                it[campaignId] = character.campaignId
                it[player] = character.player
                it[name] = character.name
                it[imageId] = character.imageId
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
        return character
    }
}
