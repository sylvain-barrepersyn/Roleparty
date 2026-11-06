package app.roleparty.core.outbox

import app.roleparty.core.rabbitmq.RabbitMqMessage
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import org.slf4j.Logger

private const val RELAY_LOCK = 7_277_100_002L
internal const val BATCH_SIZE = 100

private val POLL_INTERVAL = 1.seconds
private val CLEANUP_INTERVAL = 1.hours
internal val RETENTION = 7.days

/**
 * Publishes stored events in order. Events can be published twice, if the relay stops between a publication and its
 * commit, but they're never lost as they're only marked as published once the broker has confirmed them.
 */
internal class OutboxRelay(
    private val database: R2dbcDatabase,
    private val publish: suspend (RabbitMqMessage) -> Unit,
    private val clock: Clock,
    log: Logger,
) {
    private val failureLog = RelayFailureLog(log)

    suspend fun run() {
        var nextCleanup = clock.now()

        while (currentCoroutineContext().isActive) {
            val published = try {
                if (clock.now() >= nextCleanup) {
                    deleteExpired()
                    nextCleanup = clock.now() + CLEANUP_INTERVAL
                }
                publishPending()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                failureLog.failed(exception)
                0
            }

            if (published < BATCH_SIZE) delay(POLL_INTERVAL)
        }
    }

    /** Only one relay works at a time across instances, as concurrent ones would mix up the order of events. */
    suspend fun publishPending(): Int = suspendTransaction(database) {
        if (!tryLock()) return@suspendTransaction 0

        var published = 0
        for ((val position, val message) in pendingEvents()) {
            if (!tryPublish(message)) return@suspendTransaction published

            OutboxEvents.update({ OutboxEvents.position eq position }) { it[publishedAt] = clock.now() }
            published++
        }
        failureLog.succeeded()
        published
    }

    suspend fun deleteExpired(retention: Duration = RETENTION) {
        val limit: Instant = clock.now() - retention
        suspendTransaction(database) {
            OutboxEvents.deleteWhere { publishedAt less limit }
        }
    }

    private suspend fun R2dbcTransaction.tryLock(): Boolean =
        exec("SELECT pg_try_advisory_xact_lock($RELAY_LOCK)") { it.get(0) as Boolean }
            ?.first()
            ?: false

    private suspend fun pendingEvents(): List<PendingEvent> =
        OutboxEvents.selectAll()
            .where { OutboxEvents.publishedAt.isNull() }
            .orderBy(OutboxEvents.position, SortOrder.ASC)
            .limit(BATCH_SIZE)
            .map {
                PendingEvent(
                    position = it[OutboxEvents.position],
                    message = RabbitMqMessage(
                        id = it[OutboxEvents.id],
                        exchange = it[OutboxEvents.exchange],
                        type = it[OutboxEvents.type],
                        body = it[OutboxEvents.payload],
                    ),
                )
            }
            .toList()

    /** A failure stops the batch, as publishing the next events would mix up their order. */
    private suspend fun tryPublish(message: RabbitMqMessage): Boolean =
        try {
            publish(message)
            true
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            failureLog.failed(exception)
            false
        }

    private data class PendingEvent(val position: Long, val message: RabbitMqMessage)
}
