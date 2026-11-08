package app.roleparty.communication.hostinger

import app.roleparty.communication.commons.Email
import app.roleparty.communication.commons.EmailSender
import app.roleparty.core.commons.requireValueOrFile
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val API_TOKEN = "HOSTINGER_API_TOKEN"
private const val MAILBOX_ID = "HOSTINGER_MAILBOX_ID"
private const val BASE_URL = "HOSTINGER_BASE_URL"
private const val DEFAULT_BASE_URL = "https://api.mail.hostinger.com"
private const val DISPLAY_NAME = "Roleparty"

internal class HostingerEmailSender(
    private val httpClient: HttpClient,
    private val apiToken: String,
    private val mailboxId: String,
    private val baseUrl: String,
) : EmailSender {

    override suspend fun send(email: Email) {
        val response = httpClient.post("$baseUrl/api/v1/mailboxes/$mailboxId/send") {
            bearerAuth(apiToken)
            contentType(ContentType.Application.Json)
            setBody(
                Json.encodeToString(
                    HostingerEmail(
                        to = listOf(email.to.toString()),
                        displayName = DISPLAY_NAME,
                        subject = email.subject,
                        text = email.text,
                        html = email.html,
                    )
                )
            )
        }

        check(response.status == HttpStatusCode.NoContent) { "Hostinger refused an email with status ${response.status}" }
    }

    @Serializable
    private data class HostingerEmail(
        val to: List<String>,
        val displayName: String,
        val subject: String,
        val text: String,
        val html: String,
    )
}

fun Application.hostingerEmailSender(env: Map<String, String> = System.getenv()): EmailSender {
    val httpClient = HttpClient(CIO) { expectSuccess = false }
    monitor.subscribe(ApplicationStopped) { httpClient.close() }

    return HostingerEmailSender(
        httpClient = httpClient,
        apiToken = env.requireValueOrFile(API_TOKEN),
        mailboxId = env.requireValueOrFile(MAILBOX_ID),
        baseUrl = env[BASE_URL] ?: DEFAULT_BASE_URL,
    )
}
