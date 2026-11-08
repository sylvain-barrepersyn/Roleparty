package app.roleparty.campaign.library.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryMetadataRequestBody
import app.roleparty.campaign.library.frontend.container.data.HttpLibraryApi
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.installBackend
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import java.net.ServerSocket

class HttpLibraryApiTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val port = ServerSocket(0).use { it.localPort }
    private val campaignId = CampaignId.generate()
    private val entryId = LibraryEntryId.generate()
    private val received = mutableListOf<Any>()

    private val server = embeddedServer(Netty, port = port) {
        install(ContentNegotiation) { json() }
        routing {
            put("/v1/campaigns/{campaignId}/library/{entryId}/metadata") {
                received += call.parameters["campaignId"] + "/" + call.parameters["entryId"]
                received += call.receive<LibraryEntryMetadataRequestBody>()
                call.respond(HttpStatusCode.NoContent)
            }
            post("/v1/campaigns/{campaignId}/library") {
                call.respondText(
                    """{"code":"campaign.library.blank-title","message":"Title it."}""",
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest,
                )
            }
        }
    }.start(wait = false)

    private val client = HttpClient(CIO) { installBackend("http://localhost:$port") }
    private val api = HttpLibraryApi(client)

    init {
        afterTest {
            client.close()
            server.stop(0, 0)
        }

        test("entry metadata should be sent to the entry of the campaign") {
            api.changeEntryMetadata(campaignId, entryId, "Village", "Foggy").shouldBeSuccess()
            received shouldContainExactly listOf("$campaignId/$entryId", LibraryEntryMetadataRequestBody("Village", "Foggy"))
        }

        test("a refusal should carry the message of the backend") {
            api.createEntry(campaignId, " ", "", ImageId.generate()).shouldBeFailure(BackendFailure.Rejected("campaign.library.blank-title", "Title it."))
        }
    }
}
