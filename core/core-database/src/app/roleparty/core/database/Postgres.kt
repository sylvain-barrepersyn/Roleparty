package app.roleparty.core.database

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

class Postgres internal constructor(
    val database: R2dbcDatabase,
    private val close: () -> Unit,
    private val jdbcUrl: String,
    private val user: String,
    private val password: String,
) {

    /**
     * Applies the pending scripts of `resources/db/migration/<schema>` to [schema], creating it if needed, and records
     * them in `<schema>.flyway_schema_history`. Flyway makes concurrently starting instances wait for each other.
     */
    fun migrate(schema: String) {
        Flyway.configure()
            .dataSource(jdbcUrl, user, password)
            .schemas(schema)
            .locations("classpath:db/migration/$schema")
            .failOnMissingLocations(true)
            .load()
            .migrate()
    }

    fun close() = close.invoke()
}
