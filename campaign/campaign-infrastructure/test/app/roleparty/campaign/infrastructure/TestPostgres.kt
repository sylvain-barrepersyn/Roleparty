package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredInvitations
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.campaign.test.CampaignStores
import app.roleparty.core.database.connectPostgres
import kotlin.time.Clock
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
        playerCharacters = InfrastructuredPlayerCharacters(database),
    )

    fun sql(query: String): List<String> =
        container.execInContainer("psql", "-U", container.username, "-d", container.databaseName, "-Atc", query)
            .stdout.trim().lines().filter(String::isNotEmpty)
}
