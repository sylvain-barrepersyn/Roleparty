package app.roleparty.security.infrastructure

import app.roleparty.core.database.connectPostgres
import app.roleparty.security.database.setUpSecuritySchema
import kotlinx.coroutines.runBlocking
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
    ).apply { setUpSecuritySchema() }

    val database get() = postgres.database

    fun clear(table: String) = runBlocking {
        suspendTransaction(database) { exec("TRUNCATE security.$table") }
    }
}
