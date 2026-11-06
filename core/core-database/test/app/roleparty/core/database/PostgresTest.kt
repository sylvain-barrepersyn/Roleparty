package app.roleparty.core.database

import io.ktor.server.testing.testApplication
import kotlinx.coroutines.flow.first
import org.flywaydb.core.api.FlywayException
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.r2dbc.deleteAll
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.postgresql.PostgreSQLContainer

class PostgresTest {

    private object Characters : Table("$SCHEMA.character") {
        val name = text("name")
        val age = integer("age").nullable()
    }

    @Test
    fun `should apply the scripts of the schema so that exposed can use its tables`() = testApplication {
        // Given
        val postgres = application.loadPostgres(container.env())

        // When
        postgres.migrate(SCHEMA)

        // Then
        val age = suspendTransaction(postgres.database) {
            Characters.deleteAll()
            Characters.insert { it[name] = "Gimli"; it[age] = 139 }
            Characters.selectAll().first()[Characters.age]
        }
        assertEquals(139, age)
    }

    @Test
    fun `should keep the history of the scripts in the schema and apply them only once`() = testApplication {
        // Given
        val postgres = application.loadPostgres(container.env())

        // When
        postgres.migrate(SCHEMA)
        postgres.migrate(SCHEMA)

        // Then
        val history = sql("select version, script, success from $SCHEMA.flyway_schema_history where version is not null order by installed_rank")
        assertEquals(
            listOf(
                "0.1.0|V0.1.0__create_character_table.sql|t",
                "0.2.0|V0.2.0__add_character_age.sql|t",
            ),
            history,
        )
    }

    @Test
    fun `should fail when a schema has no scripts`() = testApplication {
        // Given
        val postgres = application.loadPostgres(container.env())

        // When / Then
        assertThrows<FlywayException> { postgres.migrate("unknown_schema") }
    }

    private fun sql(query: String): List<String> =
        container.execInContainer("psql", "-U", container.username, "-d", container.databaseName, "-Atc", query)
            .stdout.trim().lines()

    private fun PostgreSQLContainer.env() = mapOf(
        "POSTGRES_HOST" to host,
        "POSTGRES_PORT" to firstMappedPort.toString(),
        "POSTGRES_USER" to username,
        "POSTGRES_PASSWORD" to password,
        "POSTGRES_DATABASE_NAME" to databaseName,
    )

    private companion object {
        const val SCHEMA = "migration_test"
        val container = PostgreSQLContainer("postgres:18").apply { start() }
    }
}
