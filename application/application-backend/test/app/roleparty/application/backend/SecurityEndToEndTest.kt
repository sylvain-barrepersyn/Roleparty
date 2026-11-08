package app.roleparty.application.backend

import app.roleparty.account.commons.AccountId
import app.roleparty.account.infrastructure.loadAccountModule
import app.roleparty.communication.commons.Email
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.communication.commons.EmailSender
import app.roleparty.core.database.loadPostgres
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import app.roleparty.security.contract.web.AuthenticationResponseBody
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.net.ServerSocket
import java.net.URI
import java.net.http.WebSocket
import java.net.http.WebSocketHandshakeException
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutionException
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.rabbitmq.RabbitMQContainer

private val CODE = Regex("""\b[A-HJKMNP-Z2-9]{8}\b""")

class SecurityEndToEndTest {

    private val port = ServerSocket(0).use { it.localPort }
    private val keys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()

    private val env = mapOf(
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
    )

    private val sentEmails = CopyOnWriteArrayList<Email>()

    private val server = embeddedServer(Netty, port = port) {
        backend(env, EmailSender { sentEmails += it })
        routing {
            authenticated {
                get("/test/me") { call.respondText(call.user().accountId.toString()) }
            }
        }
    }.start(wait = false)

    private val client = HttpClient(CIO) { expectSuccess = false }
    private val baseUrl = "http://localhost:$port"

    private val email = "player-${Uuid.generateV7()}@roleparty.app"
    private val password = "a-valid-password"
    private val newPassword = "another-valid-password"

    @AfterEach
    fun stop() {
        client.close()
        server.stop(0, 0)
    }

    private suspend fun postJson(path: String, body: String, language: String = "en"): HttpResponse =
        client.post("$baseUrl$path") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.AcceptLanguage, language)
            setBody(body)
        }

    private suspend fun signUp(email: String = this.email, password: String = this.password, language: String = "en") =
        postJson("/v1/sign-up", """{"emailAddress":"$email","password":"$password"}""", language)

    private suspend fun verify(code: String, email: String = this.email) =
        postJson("/v1/verify-email", """{"emailAddress":"$email","code":"$code"}""")

    private suspend fun signIn(email: String = this.email, password: String = this.password, language: String = "en") =
        postJson("/v1/sign-in", """{"emailAddress":"$email","password":"$password"}""", language)

    private suspend fun refresh(refreshToken: String) = postJson("/v1/refresh", """{"refreshToken":"$refreshToken"}""")

    private suspend fun tokens(response: HttpResponse): AuthenticationResponseBody = Json.decodeFromString(response.bodyAsText())

    private suspend fun emailsTo(expected: Int, email: String = this.email): List<Email> = withContext(Dispatchers.IO) {
        withTimeout(10.seconds) {
            while (sentEmails.count { it.to.toString() == email } < expected) delay(20)
        }
        sentEmails.filter { it.to.toString() == email }
    }

    private suspend fun codeSent(expected: Int = 1, email: String = this.email): String =
        CODE.find(emailsTo(expected, email).last().text)!!.value

    private suspend fun givenVerifiedUser(email: String = this.email) {
        assertEquals(HttpStatusCode.Accepted, signUp(email).status)
        assertEquals(HttpStatusCode.NoContent, verify(codeSent(email = email), email).status)
    }

    private fun sql(query: String): String =
        postgres.execInContainer("psql", "-U", postgres.username, "-d", postgres.databaseName, "-Atc", query).stdout.trim()

    @Test
    fun `should only create the account and let the user in once the address is verified`() = runBlocking {
        // When
        val signUp = signUp()

        // Then
        assertEquals(HttpStatusCode.Accepted, signUp.status)
        assertEquals("", sql("select id from account.account where email_address = '$email'"))
        assertEquals(HttpStatusCode.Unauthorized, signIn().status)

        // When
        val verification = verify(codeSent())

        // Then
        assertEquals(HttpStatusCode.NoContent, verification.status)
        val accountId = sql("select id from account.account where email_address = '$email'")
        assertEquals(accountId, sql("select account_id from security.credential where email_address = '$email'"))

        val signIn = signIn()
        assertEquals(HttpStatusCode.OK, signIn.status)
        val me = client.get("$baseUrl/test/me") { bearerAuth(tokens(signIn).accessToken.value) }
        assertEquals(accountId, me.bodyAsText())
    }

    @Test
    fun `should not let an attacker claim the address of a victim`() = runBlocking {
        // Given
        signUp(password = "the-attacker-password")
        signUp(password = "the-victim-password")

        // When
        verify(codeSent(expected = 2))

        // Then
        assertEquals(HttpStatusCode.OK, signIn(password = "the-victim-password").status)
        assertEquals(HttpStatusCode.Unauthorized, signIn(password = "the-attacker-password").status)
    }

    @Test
    fun `should treat addresses differing by case as the same`() = runBlocking {
        // Given
        givenVerifiedUser()

        // When / Then
        assertEquals(HttpStatusCode.OK, signIn(email = email.uppercase()).status)
    }

    @Test
    fun `should send translated emails, and warn the owner when their address is used again`() = runBlocking {
        // When
        signUp(language = "fr")

        // Then
        assertEquals("Confirmez votre adresse e-mail Roleparty", emailsTo(1).single().subject)

        // When
        verify(codeSent())
        val secondSignUp = signUp(password = newPassword)

        // Then
        assertEquals(HttpStatusCode.Accepted, secondSignUp.status)
        assertEquals("Someone tried to create a Roleparty account with your address", emailsTo(2).last().subject)
        assertEquals(HttpStatusCode.OK, signIn().status)
        assertEquals(HttpStatusCode.Unauthorized, signIn(password = newPassword).status)
    }

    @Test
    fun `should not store codes or refresh tokens in clear`() = runBlocking {
        // Given
        signUp()
        val code = codeSent()

        // Then
        assertFalse(code in sql("select code_hash from security.email_code where email_address = '$email'"))

        // When
        verify(code)
        val refreshToken = tokens(signIn()).refreshToken.value

        // Then
        assertFalse(refreshToken in sql("select string_agg(token_hash, ',') from security.refresh_token"))
    }

    @Test
    fun `should refuse a protected route without a valid access token`() = runBlocking {
        // Given
        givenVerifiedUser()
        val tokens = tokens(signIn())
        val forged = JWT.create().withSubject(AccountId.generate().toString()).sign(Algorithm.HMAC256(keys.public.encoded))

        // When / Then
        assertEquals(HttpStatusCode.Unauthorized, client.get("$baseUrl/test/me").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("$baseUrl/test/me") { bearerAuth(tokens.refreshToken.value) }.status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("$baseUrl/test/me") { bearerAuth(forged) }.status)
    }

    @Test
    fun `should survive concurrent refreshes of a token on a real database`() = runBlocking {
        // Given
        givenVerifiedUser()
        val refreshToken = tokens(signIn()).refreshToken.value

        // When
        val responses = List(4) { async(Dispatchers.IO) { refresh(refreshToken) } }.awaitAll()

        // Then
        assertEquals(List(4) { HttpStatusCode.Created }, responses.map { it.status })
        val tokens = responses.map { tokens(it).refreshToken.value }
        assertEquals(4, tokens.toSet().size)
        assertEquals(HttpStatusCode.Created, refresh(tokens.first()).status)
    }

    @Test
    fun `should sign out only the current device, or all of them when asked`() = runBlocking {
        // Given
        givenVerifiedUser()
        val first = tokens(signIn())
        val second = tokens(signIn())
        val third = tokens(signIn())

        // When
        val signOut = postJson("/v1/sign-out", """{"refreshToken":"${first.refreshToken.value}"}""")

        // Then
        assertEquals(HttpStatusCode.NoContent, signOut.status)
        assertEquals(HttpStatusCode.Unauthorized, refresh(first.refreshToken.value).status)
        val secondRotated = refresh(second.refreshToken.value)
        assertEquals(HttpStatusCode.Created, secondRotated.status)

        // When
        val everywhere = client.post("$baseUrl/v1/sign-out-everywhere") { bearerAuth(third.accessToken.value) }

        // Then
        assertEquals(HttpStatusCode.NoContent, everywhere.status)
        assertEquals(HttpStatusCode.Unauthorized, refresh(tokens(secondRotated).refreshToken.value).status)
        assertEquals(HttpStatusCode.Unauthorized, refresh(third.refreshToken.value).status)
    }

    @Test
    fun `should reset a forgotten password with an emailed code and revoke every session`() = runBlocking {
        // Given
        givenVerifiedUser()
        val session = tokens(signIn())

        // When
        val request = postJson("/v1/password-reset/request", """{"emailAddress":"$email"}""")
        val code = codeSent(expected = 2)
        val confirmation = postJson("/v1/password-reset/confirm", """{"emailAddress":"$email","code":"$code","newPassword":"$newPassword"}""")

        // Then
        assertEquals(HttpStatusCode.Accepted, request.status)
        assertEquals(HttpStatusCode.NoContent, confirmation.status)
        assertEquals(HttpStatusCode.Unauthorized, refresh(session.refreshToken.value).status)
        assertEquals(HttpStatusCode.Unauthorized, signIn().status)
        assertEquals(HttpStatusCode.OK, signIn(password = newPassword).status)
    }

    @Test
    fun `should not reveal whether an address exists when asking for a reset`() = runBlocking {
        // When
        val response = postJson("/v1/password-reset/request", """{"emailAddress":"$email"}""")

        // Then
        assertEquals(HttpStatusCode.Accepted, response.status)
        withContext(Dispatchers.IO) { delay(200) }
        assertTrue(sentEmails.none { it.to.toString() == email })
    }

    @Test
    fun `should change the password of a signed in user`() = runBlocking {
        // Given
        givenVerifiedUser()
        val session = tokens(signIn())

        // When
        val wrong = client.put("$baseUrl/v1/password") {
            bearerAuth(session.accessToken.value)
            contentType(ContentType.Application.Json)
            setBody("""{"currentPassword":"wrong-password","newPassword":"$newPassword"}""")
        }
        val right = client.put("$baseUrl/v1/password") {
            bearerAuth(session.accessToken.value)
            contentType(ContentType.Application.Json)
            setBody("""{"currentPassword":"$password","newPassword":"$newPassword"}""")
        }

        // Then
        assertEquals(HttpStatusCode.Unauthorized, wrong.status)
        assertEquals(HttpStatusCode.NoContent, right.status)
        assertEquals(HttpStatusCode.OK, signIn(password = newPassword).status)
        assertEquals(HttpStatusCode.Unauthorized, refresh(session.refreshToken.value).status)
    }

    @Test
    fun `should sign in with the new email address after the account changed it`() = runBlocking {
        // Given
        givenVerifiedUser()
        val accountId = AccountId.from(sql("select id from account.account where email_address = '$email'"))
        val newEmail = "renamed-$email"
        val accountModule = loadAccountModule(server.application.loadPostgres(env))

        // When
        accountModule.changeAccountEmailAddress(accountId, EmailAddress.from(newEmail))

        // Then
        withContext(Dispatchers.IO) {
            withTimeout(20.seconds) {
                while (sql("select email_address from security.credential where account_id = '$accountId'") != newEmail) delay(200)
            }
        }
        assertEquals(HttpStatusCode.OK, signIn(newEmail).status)
        assertEquals(HttpStatusCode.Unauthorized, signIn(email).status)
    }

    @Test
    fun `should translate errors in the language of the request`() = runBlocking {
        // When
        val english = signIn(language = "en")
        val french = signIn(language = "fr")

        // Then
        assertEquals(HttpStatusCode.Unauthorized, english.status)
        assertTrue("security.invalid-credentials" in english.bodyAsText())
        assertTrue("The email address or the password is incorrect." in english.bodyAsText())
        assertTrue("mot de passe est incorrect" in french.bodyAsText())
    }

    @Test
    fun `should reject an invalid email address and an invalid password`() = runBlocking {
        // When
        val invalidEmail = signUp(email = "not-an-email")
        val shortPassword = signUp(password = "short")

        // Then
        assertEquals(HttpStatusCode.BadRequest, invalidEmail.status)
        assertTrue("communication.invalid-email-address" in invalidEmail.bodyAsText())
        assertEquals(HttpStatusCode.BadRequest, shortPassword.status)
        assertTrue("security.invalid-password" in shortPassword.bodyAsText())
    }

    @Test
    fun `should refuse a body too large before reading it`() = runBlocking {
        // When
        val response = signIn(password = "x".repeat(5000))

        // Then
        assertEquals(HttpStatusCode.PayloadTooLarge, response.status)
    }

    @Test
    fun `should limit credential attempts per client, across addresses`() = runBlocking {
        // When
        val statuses = List(11) { signIn(email = "someone-$it-$email", password = "wrong-password").status }

        // Then
        assertEquals(List(10) { HttpStatusCode.Unauthorized } + HttpStatusCode.TooManyRequests, statuses)
    }

    @Test
    fun `should allow many more refreshes than credential attempts`() = runBlocking {
        // Given
        givenVerifiedUser()
        var refreshToken = tokens(signIn()).refreshToken.value

        // When
        val statuses = List(15) {
            val response = refresh(refreshToken)
            refreshToken = tokens(response).refreshToken.value
            response.status
        }

        // Then
        assertEquals(List(15) { HttpStatusCode.Created }, statuses)
    }

    @Test
    fun `should refuse the account rpc endpoint without its token`() {
        // When
        val failure = assertThrows<ExecutionException> {
            java.net.http.HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:$port/rpc/account"), object : WebSocket.Listener {})
                .get()
        }

        // Then
        val handshake = failure.cause as WebSocketHandshakeException
        assertEquals(HttpStatusCode.Unauthorized.value, handshake.response.statusCode())
    }

    private companion object {
        val postgres = PostgreSQLContainer("postgres:18").apply { start() }
        val rabbitMq = RabbitMQContainer("rabbitmq:4.2.3-management-alpine").apply { start() }
    }
}
