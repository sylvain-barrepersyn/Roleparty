package app.roleparty.security.frontend.container

import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.frontend.container.data.HttpSecurityApi
import app.roleparty.security.frontend.container.data.SecurityFailure
import app.roleparty.security.frontend.container.data.StoredSession
import app.roleparty.security.frontend.container.data.installApplicationAuthentication
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.net.ServerSocket

class HttpSecurityClientTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val port = ServerSocket(0).use { it.localPort }
    private var signInStatus = HttpStatusCode.OK
    private var signInBody = tokens("access", "refresh")
    private var refreshStatus = HttpStatusCode.Created
    private var refreshBody = tokens("fresh-access", "fresh-refresh")
    private var validAccessToken = "fresh-access"

    private fun tokens(access: String, refresh: String) =
        """{"accessToken":{"value":"$access","expirationDate":"2026-11-07T12:00:00Z"},"refreshToken":{"value":"$refresh","expirationDate":"2026-11-21T12:00:00Z"}}"""

    private val server = embeddedServer(Netty, port = port) {
        routing {
            post("/v1/sign-in") { call.respondText(signInBody, ContentType.Application.Json, signInStatus) }
            post("/v1/refresh") { call.respondText(refreshBody, ContentType.Application.Json, refreshStatus) }
            get("/whoami") { call.respondText(call.request.header(HttpHeaders.Authorization).orEmpty()) }
            get("/protected") {
                if (call.request.header(HttpHeaders.Authorization) == "Bearer $validAccessToken") call.respondText("secret")
                else call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }.start(wait = false)

    private val plainClient = HttpClient(CIO) { expectSuccess = false }
    private val api = HttpSecurityApi(plainClient, "http://localhost:$port")

    init {
        afterTest {
            plainClient.close()
            server.stop(0, 0)
        }

        test("should read tokens from a successful response") {
            api.signIn("gimli@roleparty.app", "a-valid-password").shouldBeSuccess() shouldBe StoredSession("access", "refresh")
        }

        test("should read the code and the message of a refusal") {
            // Given
            signInStatus = HttpStatusCode.Unauthorized
            signInBody = """{"code":"security.invalid-credentials","message":"Incorrect."}"""

            // When / Then
            api.signIn("gimli@roleparty.app", "a-wrong-password") shouldBeFailure
                SecurityFailure.Rejected("security.invalid-credentials", "Incorrect.")
        }

        test("should treat a refusal without details, like a rate limit, as unexpected") {
            // Given
            signInStatus = HttpStatusCode.TooManyRequests
            signInBody = ""

            // When / Then
            api.signIn("gimli@roleparty.app", "a-valid-password") shouldBeFailure SecurityFailure.Unexpected
        }

        test("should report an unreachable backend") {
            // Given
            val closedPort = ServerSocket(0).use { it.localPort }
            val unreachable = HttpSecurityApi(plainClient, "http://localhost:$closedPort")

            // When / Then
            unreachable.signIn("gimli@roleparty.app", "a-valid-password") shouldBeFailure SecurityFailure.Unreachable
        }

        test("should send the token of the current session, even after another account signed in") {
            // Given
            val store = InMemorySessionStore(StoredSession("first-access", "first-refresh"))
            val client = HttpClient(CIO) {
                expectSuccess = false
                installApplicationAuthentication(api, store) { error("The session is still valid") }
            }
            client.get("http://localhost:$port/whoami").bodyAsText() shouldBe "Bearer first-access"

            // When
            store.session = StoredSession("second-access", "second-refresh")

            // Then
            client.get("http://localhost:$port/whoami").bodyAsText() shouldBe "Bearer second-access"
            client.close()
        }

        test("should refresh an expired access token once and retry the request") {
            // Given
            val store = InMemorySessionStore(StoredSession("expired-access", "old-refresh"))
            val client = HttpClient(CIO) {
                expectSuccess = false
                installApplicationAuthentication(api, store) { error("The session is still valid") }
            }

            // When
            val response = client.get("http://localhost:$port/protected")

            // Then
            response.bodyAsText() shouldBe "secret"
            store.session shouldBe StoredSession("fresh-access", "fresh-refresh")
            client.close()
        }

        test("should keep the session when the backend fails for another reason") {
            // Given
            refreshStatus = HttpStatusCode.InternalServerError
            refreshBody = ""
            val session = StoredSession("expired-access", "old-refresh")
            val store = InMemorySessionStore(session)
            val client = HttpClient(CIO) {
                expectSuccess = false
                installApplicationAuthentication(api, store) { error("The session should not end") }
            }

            // When
            val response = client.get("http://localhost:$port/protected")

            // Then
            response.status shouldBe HttpStatusCode.Unauthorized
            store.session shouldBe session
            client.close()
        }

        test("should end the session when the refresh token is refused") {
            // Given
            refreshStatus = HttpStatusCode.Unauthorized
            refreshBody = """{"code":"security.invalid-refresh-token","message":"Your session is no longer valid."}"""
            val store = InMemorySessionStore(StoredSession("expired-access", "old-refresh"))
            var expired = false
            val client = HttpClient(CIO) {
                expectSuccess = false
                installApplicationAuthentication(api, store) { expired = true }
            }

            // When
            val response = client.get("http://localhost:$port/protected")

            // Then
            response.status shouldBe HttpStatusCode.Unauthorized
            client.close()
            expired shouldBe true
            store.session.shouldBeNull()
        }
    }
}
