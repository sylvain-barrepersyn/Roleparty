package app.roleparty.communication.hostinger

import app.roleparty.communication.commons.Email
import app.roleparty.communication.commons.EmailAddress
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.header
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.net.ServerSocket
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HostingerEmailSenderTest {

    private val port = ServerSocket(0).use { it.localPort }
    private var status = HttpStatusCode.NoContent
    private var receivedAuthorization: String? = null
    private var receivedBody = ""

    private val server = embeddedServer(Netty, port = port) {
        routing {
            post("/api/v1/mailboxes/a-mailbox/send") {
                receivedAuthorization = call.request.header(HttpHeaders.Authorization)
                receivedBody = call.receiveText()
                call.respond(status)
            }
        }
    }.start(wait = false)

    private val sender = server.application.hostingerEmailSender(
        mapOf(
            "HOSTINGER_API_TOKEN" to "a-token",
            "HOSTINGER_MAILBOX_ID" to "a-mailbox",
            "HOSTINGER_BASE_URL" to "http://localhost:$port",
        )
    )

    private val email = Email(EmailAddress.from("gimli@roleparty.app"), "A subject", "A text", "<p>A text</p>")

    @AfterEach
    fun stop() {
        server.stop(0, 0)
    }

    @Test
    fun `should send the email to the mailbox with the token`() = runBlocking {
        // When
        sender.send(email)

        // Then
        assertEquals("Bearer a-token", receivedAuthorization)
        val body = Json.parseToJsonElement(receivedBody).jsonObject
        assertEquals("gimli@roleparty.app", body.getValue("to").jsonArray.single().jsonPrimitive.content)
        assertEquals("Roleparty", body.getValue("displayName").jsonPrimitive.content)
        assertEquals("A subject", body.getValue("subject").jsonPrimitive.content)
        assertEquals("A text", body.getValue("text").jsonPrimitive.content)
        assertEquals("<p>A text</p>", body.getValue("html").jsonPrimitive.content)
    }

    @Test
    fun `should fail when hostinger refuses the email`() = runBlocking<Unit> {
        // Given
        status = HttpStatusCode.Unauthorized

        // When / Then
        assertThrows<IllegalStateException> { sender.send(email) }
    }
}
