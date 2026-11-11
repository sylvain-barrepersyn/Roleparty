package app.roleparty.campaign.story.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.contract.web.SceneRequestBody
import app.roleparty.campaign.story.frontend.container.data.HttpStoryApi
import app.roleparty.core.document.TextRun
import app.roleparty.core.document.Paragraph
import app.roleparty.core.document.Document
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.installBackend
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
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
import io.ktor.server.routing.delete
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import java.net.ServerSocket

class HttpStoryApiTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val port = ServerSocket(0).use { it.localPort }
    private val campaignId = CampaignId.generate()
    private val sceneId = SceneId.generate()
    private val received = mutableListOf<Any>()

    private val server = embeddedServer(Netty, port = port) {
        install(ContentNegotiation) { json() }
        routing {
            put("/v1/campaigns/{campaignId}/scenes/{sceneId}") {
                received += call.parameters["campaignId"] + "/" + call.parameters["sceneId"]
                received += call.receive<SceneRequestBody>()
                call.respond(HttpStatusCode.NoContent)
            }
            delete("/v1/campaigns/{campaignId}/scenes/{sceneId}") {
                call.respondText(
                    """{"code":"campaign.story.scene-not-found","message":"No such scene."}""",
                    ContentType.Application.Json,
                    HttpStatusCode.NotFound,
                )
            }
        }
    }.start(wait = false)

    private val client = HttpClient(CIO) { installBackend("http://localhost:$port") }
    private val api = HttpStoryApi(client)

    init {
        afterTest {
            client.close()
            server.stop(0, 0)
        }

        test("a scene change should be sent to the scene of the campaign") {
            api.changeScene(campaignId, sceneId, "Ambush", document("Goblins")).shouldBeSuccess()
            received shouldContainExactly listOf("$campaignId/$sceneId", SceneRequestBody("Ambush", document("Goblins")))
        }

        test("a refusal should carry the message of the backend") {
            api.deleteScene(campaignId, sceneId).shouldBeFailure(BackendFailure.Rejected("campaign.story.scene-not-found", "No such scene."))
        }
    }
}

private fun document(text: String) = Document.from(listOf(Paragraph(runs = listOf(TextRun(text)))))
