package app.roleparty.campaign.agenda.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.CampaignParticipants
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.CampaignTable
import app.roleparty.campaign.database.PlayerTable
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
}
