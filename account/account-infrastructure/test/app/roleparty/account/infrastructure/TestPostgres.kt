package app.roleparty.account.infrastructure

import app.roleparty.account.database.setUpAccountSchema
import app.roleparty.core.database.connectPostgres
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.testcontainers.postgresql.PostgreSQLContainer

internal object TestPostgres {
    private val container = PostgreSQLContainer("postgres:18").apply { start() }

    val env = mapOf(
        "POSTGRES_HOST" to container.host,
        "POSTGRES_PORT" to container.firstMappedPort.toString(),
        "POSTGRES_USER" to container.username,
        "POSTGRES_PASSWORD" to container.password,
        "POSTGRES_DATABASE_NAME" to container.databaseName,
    )

    val postgres = connectPostgres(env).apply {
        migrate("technical")
        setUpAccountSchema()
    }

    suspend fun clear() {
        suspendTransaction(postgres.database) { exec("TRUNCATE account.event, account.overview, technical.event") }
    }

    fun sql(query: String): List<String> =
        container.execInContainer("psql", "-U", container.username, "-d", container.databaseName, "-Atc", query)
            .stdout.trim().lines().filter(String::isNotEmpty)
}
