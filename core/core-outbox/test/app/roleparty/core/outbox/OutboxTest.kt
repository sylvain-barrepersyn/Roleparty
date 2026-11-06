package app.roleparty.core.outbox

import app.roleparty.core.database.Postgres
import app.roleparty.core.database.loadPostgres
import app.roleparty.core.rabbitmq.RabbitMqEvent
import app.roleparty.core.rabbitmq.RabbitMqEventHandler
import app.roleparty.core.rabbitmq.RabbitMqMessage
import app.roleparty.core.rabbitmq.loadRabbitMq
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteAll
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.rabbitmq.RabbitMQContainer

class OutboxTest {

    @Serializable
    data class SomethingHappened(val content: String) : RabbitMqEvent {
        companion object {
            const val EXCHANGE = "test.something-happened"
        }
    }

    class SomethingHappenedHandler(private val received: CompletableDeferred<Pair<Uuid, SomethingHappened>>) :
        RabbitMqEventHandler<SomethingHappened> {
        override suspend fun handle(eventId: Uuid, event: SomethingHappened) {
            received.complete(eventId to event)
        }

        companion object {
            const val QUEUE = "test.something-happened.queue"
        }
    }

    private val log = LoggerFactory.getLogger(OutboxTest::class.java)
    private val clock = Clock.System

    private fun outboxTest(block: suspend ApplicationTestBuilder.(Postgres) -> Unit) = testApplication {
        val postgres = application.loadPostgres(postgresContainer.env())
        postgres.migrate(TECHNICAL_SCHEMA)
        suspendTransaction(postgres.database) { OutboxEvents.deleteAll() }
        block(postgres)
    }

    private fun Postgres.relay(publish: suspend (RabbitMqMessage) -> Unit) = OutboxRelay(database, publish, clock, log)

    private suspend fun Postgres.enqueue(vararg contents: String) = suspendTransaction(database) {
        contents.forEach { enqueue(SomethingHappened(it), clock.now()) }
    }

    private fun String.content() = Json.decodeFromString<SomethingHappened>(this).content

    private suspend fun Postgres.rows() = suspendTransaction(database) {
        OutboxEvents.selectAll().orderBy(OutboxEvents.position).toList()
    }

    @Test
    fun `should record the table creation in the history of the schema`() = outboxTest {
        // When
        val history = postgresContainer.execInContainer(
            "psql", "-U", postgresContainer.username, "-d", postgresContainer.databaseName, "-Atc",
            "select version, success from technical.flyway_schema_history where version is not null order by installed_rank",
        ).stdout.trim()

        // Then
        assertEquals("0.1.0|t", history)
    }

    @Test
    fun `should store the event as given`() = outboxTest { postgres ->
        // Given
        val id = Uuid.generateV7()
        val occurredAt = Instant.parse("2026-11-06T17:30:15.123456Z")

        // When
        suspendTransaction(postgres.database) { enqueue(SomethingHappened("hello"), occurredAt, id) }

        // Then
        val row = postgres.rows().single()
        assertEquals(id, row[OutboxEvents.id])
        assertEquals(occurredAt, row[OutboxEvents.occurredAt])
        assertEquals(SomethingHappened::class.java.name, row[OutboxEvents.type])
        assertEquals(SomethingHappened.EXCHANGE, row[OutboxEvents.exchange])
        assertNull(row[OutboxEvents.publishedAt])
    }

    @Test
    fun `should publish events in the order they were stored whatever their ids`() = outboxTest { postgres ->
        // Given
        val published = mutableListOf<String>()
        val smallerId = Uuid.generateV7()
        val biggerId = Uuid.generateV7()
        suspendTransaction(postgres.database) {
            enqueue(SomethingHappened("first"), clock.now(), biggerId)
            enqueue(SomethingHappened("second"), clock.now(), smallerId)
        }

        // When
        postgres.relay { published += it.body.content() }.publishPending()

        // Then
        assertTrue(smallerId < biggerId)
        assertEquals(listOf("first", "second"), published)
    }

    @Test
    fun `should only log the first failure of a series in full then its recovery`() = outboxTest { postgres ->
        // Given
        var brokerAvailable = false
        val logger = LoggerFactory.getLogger("relay-failures") as ch.qos.logback.classic.Logger
        val logs = ListAppender<ILoggingEvent>().apply { start() }
        logger.addAppender(logs)
        val relay = OutboxRelay(postgres.database, { check(brokerAvailable) { "The broker is down" } }, clock, logger)
        postgres.enqueue("first")

        // When
        repeat(5) { relay.publishPending() }
        brokerAvailable = true
        relay.publishPending()
        relay.publishPending()

        // Then
        val visible = logs.list.filter { it.level.isGreaterOrEqual(Level.INFO) }
        assertEquals(listOf(Level.ERROR, Level.INFO), visible.map { it.level })
        assertNotNull(visible.first().throwableProxy)
        assertTrue(visible.last().formattedMessage.contains("5 failed attempts"), visible.last().formattedMessage)
    }

    @Test
    fun `should not store the event when the transaction rolls back`() = outboxTest { postgres ->
        // When
        assertThrows<IllegalStateException> {
            suspendTransaction(postgres.database) {
                enqueue(SomethingHappened("rolled back"), clock.now())
                error("The state change failed")
            }
        }

        // Then
        assertTrue(postgres.rows().isEmpty())
    }

    @Test
    fun `should publish a committed event to a real consumer`() = outboxTest { postgres ->
        // Given
        val rabbitMq = application.loadRabbitMq(rabbitMqContainer.env())
        val received = CompletableDeferred<Pair<Uuid, SomethingHappened>>()
        rabbitMq.registerEventHandler(SomethingHappenedHandler(received))
        val id = Uuid.generateV7()

        // When
        suspendTransaction(postgres.database) { enqueue(SomethingHappened("hello"), clock.now(), id) }
        val published = postgres.relay(rabbitMq::publish).publishPending()

        // Then
        assertEquals(1, published)
        assertEquals(id to SomethingHappened("hello"), withContext(Dispatchers.IO) { withTimeout(10.seconds) { received.await() } })
        assertNotNull(postgres.rows().single()[OutboxEvents.publishedAt])
    }

    @Test
    fun `should publish events in order and only once`() = outboxTest { postgres ->
        // Given
        val published = mutableListOf<String>()
        val relay = postgres.relay { published += it.body.content() }
        postgres.enqueue("first", "second", "third")

        // When
        relay.publishPending()
        val publishedAgain = relay.publishPending()

        // Then
        assertEquals(listOf("first", "second", "third"), published)
        assertEquals(0, publishedAgain)
    }

    @Test
    fun `should keep unconfirmed events and publish them in order later`() = outboxTest { postgres ->
        // Given
        val published = mutableListOf<String>()
        var brokerAvailable = false
        val relay = postgres.relay {
            if (!brokerAvailable && it.body.contains("second")) error("The broker is down")
            published += it.body.content()
        }
        postgres.enqueue("first", "second", "third")

        // When
        val publishedWhileDown = relay.publishPending()

        // Then
        assertEquals(1, publishedWhileDown)
        assertEquals(listOf(true, false, false), postgres.rows().map { it[OutboxEvents.publishedAt] != null })

        // When
        brokerAvailable = true
        val publishedOnceBack = relay.publishPending()

        // Then
        assertEquals(2, publishedOnceBack)
        assertEquals(listOf("first", "second", "third"), published)
    }

    @Test
    fun `should only let one relay publish at a time`() = outboxTest { postgres ->
        // Given
        val publishing = CompletableDeferred<Unit>()
        val finish = CompletableDeferred<Unit>()
        postgres.enqueue("first")
        coroutineScope {
            val busyRelay = async(Dispatchers.IO) {
                postgres.relay {
                    publishing.complete(Unit)
                    finish.await()
                }.publishPending()
            }
            withTimeout(10.seconds) { publishing.await() }

            // When
            val published = postgres.relay { error("This relay must not publish") }.publishPending()

            // Then
            assertEquals(0, published)
            finish.complete(Unit)
            assertEquals(1, busyRelay.await())
        }
    }

    @Test
    fun `should only delete published events older than the retention`() = outboxTest { postgres ->
        // Given
        val relay = postgres.relay { }
        postgres.enqueue("expired", "recent")
        relay.publishPending()
        postgres.enqueue("pending")
        suspendTransaction(postgres.database) {
            val expired = OutboxEvents.selectAll().orderBy(OutboxEvents.position).toList().first()[OutboxEvents.id]
            OutboxEvents.update({ OutboxEvents.id eq expired }) { it[publishedAt] = clock.now() - 8.days }
        }

        // When
        relay.deleteExpired()

        // Then
        val rows = postgres.rows()
        assertEquals(listOf("recent", "pending"), rows.map { it[OutboxEvents.payload].content() })
        assertNull(rows.last()[OutboxEvents.publishedAt])
    }

    private fun PostgreSQLContainer.env() = mapOf(
        "POSTGRES_HOST" to host,
        "POSTGRES_PORT" to firstMappedPort.toString(),
        "POSTGRES_USER" to username,
        "POSTGRES_PASSWORD" to password,
        "POSTGRES_DATABASE_NAME" to databaseName,
    )

    private fun RabbitMQContainer.env() = mapOf(
        "RABBITMQ_HOST" to host,
        "RABBITMQ_PORT" to amqpPort.toString(),
        "RABBITMQ_USER" to adminUsername,
        "RABBITMQ_PASSWORD" to adminPassword,
    )

    private companion object {
        val postgresContainer = PostgreSQLContainer("postgres:18").apply { start() }
        val rabbitMqContainer = RabbitMQContainer("rabbitmq:4.2.3-management-alpine").apply { start() }
    }
}
