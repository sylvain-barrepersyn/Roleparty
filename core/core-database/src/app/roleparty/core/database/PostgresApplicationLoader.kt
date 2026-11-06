package app.roleparty.core.database

import app.roleparty.core.commons.requireValueOrFile
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.util.AttributeKey
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import java.time.Duration
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig

private const val HOST = "POSTGRES_HOST"
private const val PORT = "POSTGRES_PORT"
private const val USER = "POSTGRES_USER"
private const val PASSWORD = "POSTGRES_PASSWORD"
private const val DATABASE_NAME = "POSTGRES_DATABASE_NAME"

private const val POOL_INITIAL_SIZE = 1
private const val POOL_MAX_SIZE = 5
private val POOL_MAX_IDLE_TIME = Duration.ofSeconds(30)

private val PostgresKey = AttributeKey<Postgres>("Postgres")

fun Application.loadPostgres(env: Map<String, String> = System.getenv()): Postgres {
    attributes.getOrNull(PostgresKey)?.let { return it }

    val postgres = connectPostgres(env)
    monitor.subscribe(ApplicationStopped) { postgres.close() }

    return postgres.also { attributes.put(PostgresKey, it) }
}

fun connectPostgres(env: Map<String, String>): Postgres {
    val host = env.requireValueOrFile(HOST)
    val port = env.requireValueOrFile(PORT).toInt()
    val databaseName = env.requireValueOrFile(DATABASE_NAME)
    val user = env.requireValueOrFile(USER)
    val password = env.requireValueOrFile(PASSWORD)
    val url = "r2dbc:postgresql://$host:$port/$databaseName"

    val options = ConnectionFactoryOptions.parse(url).mutate()
        .option(ConnectionFactoryOptions.USER, user)
        .option(ConnectionFactoryOptions.PASSWORD, password)
        .build()

    val pool = ConnectionPool(
        ConnectionPoolConfiguration.builder(ConnectionFactories.get(options))
            .initialSize(POOL_INITIAL_SIZE)
            .maxSize(POOL_MAX_SIZE)
            .maxIdleTime(POOL_MAX_IDLE_TIME)
            .build()
    )

    return Postgres(
        database = R2dbcDatabase.connect(pool, R2dbcDatabaseConfig.Builder().apply { setUrl(url) }),
        close = pool::dispose,
        jdbcUrl = "jdbc:postgresql://$host:$port/$databaseName",
        user = user,
        password = password,
    )
}
