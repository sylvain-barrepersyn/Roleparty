package app.roleparty.core.outbox

import app.roleparty.core.database.Postgres
import app.roleparty.core.rabbitmq.RabbitMq
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import kotlin.time.Clock
import kotlinx.coroutines.launch

/** Migrates the `technical` schema, holding the outbox, and starts publishing its events to RabbitMQ. */
fun Application.loadOutbox(postgres: Postgres, rabbitMq: RabbitMq, clock: Clock = Clock.System) {
    postgres.migrate(TECHNICAL_SCHEMA)

    val relay = OutboxRelay(postgres.database, rabbitMq::publish, clock, environment.log)
    val job = launch { relay.run() }

    monitor.subscribe(ApplicationStopping) { job.cancel() }
}
