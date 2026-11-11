package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.database.GameSessionTable
import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredGameSessions
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredInvitations
import app.roleparty.campaign.test.CampaignStores
import app.roleparty.core.database.connectPostgres
import kotlin.time.Clock
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
        migrate("technical")
        setUpCampaignSchema()
    }

    val database get() = postgres.database

    fun stores() = CampaignStores(
        campaigns = InfrastructuredCampaigns(database, Clock.System),
        invitations = InfrastructuredInvitations(database),
        gameSessions = InfrastructuredGameSessions(database),
        scheduleGameSession = { campaignId, date ->
            suspendTransaction(database) {
                GameSessionTable.insert {
                    it[id] = GameSessionId.generate()
                    it[GameSessionTable.campaignId] = campaignId
                    it[GameSessionTable.date] = date
                }
            }
        },
    )

    fun sql(query: String): List<String> =
        container.execInContainer("psql", "-U", container.username, "-d", container.databaseName, "-Atc", query)
            .stdout.trim().lines().filter(String::isNotEmpty)
}
