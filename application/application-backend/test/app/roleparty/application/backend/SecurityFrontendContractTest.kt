package app.roleparty.application.backend

import app.roleparty.communication.commons.Email
import app.roleparty.communication.commons.EmailSender
import app.roleparty.core.commons.Outcome
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import app.roleparty.security.frontend.container.data.HttpSecurityApi
import app.roleparty.security.frontend.container.data.SecurityFailure
import app.roleparty.security.frontend.container.data.SessionStore
import app.roleparty.security.frontend.container.data.StoredSession
import app.roleparty.security.frontend.container.data.installApplicationAuthentication
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.net.ServerSocket
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.rabbitmq.RabbitMQContainer

private val EMAIL_CODE = Regex("""\b[A-HJKMNP-Z2-9]{8}\b""")

class SecurityFrontendContractTest {

    private val port = ServerSocket(0).use { it.localPort }
    private val keys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    private val sentEmails = CopyOnWriteArrayList<Email>()

    private val server = embeddedServer(Netty, port = port) {
        backend(
            mapOf(
                "POSTGRES_HOST" to postgres.host,
                "POSTGRES_PORT" to postgres.firstMappedPort.toString(),
                "POSTGRES_USER" to postgres.username,
                "POSTGRES_PASSWORD" to postgres.password,
                "POSTGRES_DATABASE_NAME" to postgres.databaseName,
                "RABBITMQ_HOST" to rabbitMq.host,
                "RABBITMQ_PORT" to rabbitMq.amqpPort.toString(),
                "RABBITMQ_USER" to rabbitMq.adminUsername,
                "RABBITMQ_PASSWORD" to rabbitMq.adminPassword,
                "JWT_PUBLIC_KEY" to Base64.encode(keys.public.encoded),
                "JWT_PRIVATE_KEY" to Base64.encode(keys.private.encoded),
                "ACCOUNT_RPC_URL" to "ws://localhost:$port/rpc/account",
                "ACCOUNT_RPC_TOKEN" to "an-rpc-token-long-enough-for-the-test",
                "MEDIA_RPC_TOKEN" to "an-rpc-token-long-enough-for-the-test",
            ),
            EmailSender { sentEmails += it },
        )
        routing {
            authenticated {
                get("/test/me") { call.respondText(call.user().accountId.toString()) }
            }
        }
    }.start(wait = false)

    private val baseUrl = "http://localhost:$port"
    private val httpClient = HttpClient(CIO) { expectSuccess = false }
    private val api = HttpSecurityApi(httpClient, baseUrl)

    private val email = "player-${Uuid.generateV7()}@roleparty.app"
    private val password = "a-valid-password"
    private val newPassword = "another-valid-password"

    @AfterEach
    fun stop() {
        httpClient.close()
        server.stop(0, 0)
    }

    private suspend fun codeSent(expected: Int): String = withContext(Dispatchers.IO) {
        withTimeout(10.seconds) {
            while (sentEmails.size < expected) delay(20)
        }
        EMAIL_CODE.find(sentEmails.last().text)!!.value
    }

    private fun <T> Outcome<T, SecurityFailure>.success(): T = (this as Outcome.Success).value

    @Test
    fun `the client of the application should go through every flow with the real backend`() = runBlocking {
        // Sign up and verify
        api.signUp(email, password).success()
        api.verifyEmailAddress(email, codeSent(expected = 1)).success()

        // Sign in, refresh and sign out
        val session = api.signIn(email, password).success()
        val refreshed = api.refresh(session.refreshToken).success()
        api.signOut(refreshed.refreshToken).success()
        assertTrue(api.refresh(refreshed.refreshToken) is Outcome.Failure)

        // Reset the password
        api.requestPasswordReset(email).success()
        api.resetPassword(email, codeSent(expected = 2), newPassword).success()
        assertTrue(api.signIn(email, password) is Outcome.Failure)
        api.signIn(email, newPassword).success()
        Unit
    }

    @Test
    fun `the client should show the translated message of a refusal`() = runBlocking {
        // Given
        val defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.FRENCH)

        try {
            // When
            val failure = (api.signIn(email, password) as Outcome.Failure).error

            // Then
            val rejected = failure as SecurityFailure.Rejected
            assertEquals("security.invalid-credentials", rejected.code)
            assertTrue("mot de passe est incorrect" in rejected.message, rejected.message)
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun `an expired access token should be refreshed transparently, with a token the backend really issued`() = runBlocking {
        // Given
        api.signUp(email, password).success()
        api.verifyEmailAddress(email, codeSent(expected = 1)).success()
        val session = api.signIn(email, password).success()
        val store = object : SessionStore {
            var current: StoredSession? = session.copy(accessToken = "not-a-valid-access-token")
            override suspend fun load() = current
            override suspend fun save(session: StoredSession) { current = session }
            override suspend fun clear() { current = null }
        }
        val client = HttpClient(CIO) {
            expectSuccess = false
            installApplicationAuthentication(api, store) { error("The session is still valid") }
        }

        // When
        val response = client.get("$baseUrl/test/me")

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().isNotBlank())
        client.close()
    }

    private companion object {
        val postgres = PostgreSQLContainer("postgres:18").apply { start() }
        val rabbitMq = RabbitMQContainer("rabbitmq:4.2.3-management-alpine").apply { start() }
    }
}
