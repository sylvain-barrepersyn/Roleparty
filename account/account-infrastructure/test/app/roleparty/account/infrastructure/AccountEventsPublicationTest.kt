package app.roleparty.account.infrastructure

import app.roleparty.account.commons.AccountRole
import app.roleparty.account.event.AccountRegistered
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.outbox.loadOutbox
import app.roleparty.core.rabbitmq.RabbitMqEventHandler
import app.roleparty.core.rabbitmq.loadRabbitMq
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.testcontainers.rabbitmq.RabbitMQContainer

class AccountRegisteredTestHandler(private val received: CompletableDeferred<Pair<Uuid, AccountRegistered>>) :
    RabbitMqEventHandler<AccountRegistered> {

    override suspend fun handle(eventId: Uuid, event: AccountRegistered) {
        received.complete(eventId to event)
    }

    companion object {
        const val QUEUE = "test.account.account-registered"
    }
}

class AccountEventsPublicationTest : FunSpec({

    test("a registration should reach a consumer of the public event through the outbox") {
        val rabbitMqContainer = RabbitMQContainer("rabbitmq:4.2.3-management-alpine").apply { start() }
        TestPostgres.clear()

        testApplication {
            // Given
            val rabbitMq = application.loadRabbitMq(
                mapOf(
                    "RABBITMQ_HOST" to rabbitMqContainer.host,
                    "RABBITMQ_PORT" to rabbitMqContainer.amqpPort.toString(),
                    "RABBITMQ_USER" to rabbitMqContainer.adminUsername,
                    "RABBITMQ_PASSWORD" to rabbitMqContainer.adminPassword,
                )
            )
            val received = CompletableDeferred<Pair<Uuid, AccountRegistered>>()
            rabbitMq.registerEventHandler(AccountRegisteredTestHandler(received))
            application.loadOutbox(TestPostgres.postgres, rabbitMq)
            val module = loadAccountModule(TestPostgres.postgres)
            val email = EmailAddress.from("gimli@roleparty.app")

            // When
            val outcome = module.registerAccount(email)

            // Then
            val accountId = (outcome as Outcome.Success).value
            val delivery = withContext(Dispatchers.IO) { withTimeout(15.seconds) { received.await() } }
            delivery.second shouldBe AccountRegistered(accountId, email, AccountRole.USER)
            delivery.first.toString() shouldBe TestPostgres.sql("select id from technical.event where payload->>'accountId' = '$accountId'").single()
        }

        rabbitMqContainer.stop()
    }
})
