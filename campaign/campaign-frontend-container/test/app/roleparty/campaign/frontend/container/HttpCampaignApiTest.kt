package app.roleparty.campaign.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.CampaignCreationRequestBody
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.container.data.HttpCampaignApi
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.installBackend
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
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
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.net.ServerSocket

class HttpCampaignApiTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val port = ServerSocket(0).use { it.localPort }
    private val campaignId = CampaignId.generate()
    private val imageId = ImageId.generate()
    private val overview = CampaignOverviewResponseBody(campaignId, "Curse of Strahd", imageId, 4)
    private val received = mutableListOf<Any>()

    private val server = embeddedServer(Netty, port = port) {
        install(ContentNegotiation) { json() }
        routing {
            get("/v1/campaigns") {
                received += "participation=${call.request.queryParameters["participation"]}"
                call.respond(listOf(overview))
            }
            post("/v1/campaigns") {
                received += call.receive<CampaignCreationRequestBody>()
                call.respondText("""{"id":"$campaignId"}""", ContentType.Application.Json, HttpStatusCode.Created)
            }
            post("/v1/campaigns/join") {
                call.respondText(
                    """{"code":"campaign.invalid-invitation-code","message":"No such code."}""",
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest,
                )
            }
        }
    }.start(wait = false)

    private val client = HttpClient(CIO) { installBackend("http://localhost:$port") }
    private val api = HttpCampaignApi(client)

    init {
        afterTest {
            client.close()
            server.stop(0, 0)
        }

        test("campaigns should be listed by participation") {
            api.campaigns(Participation.PLAYER).shouldBeSuccess() shouldBe listOf(overview)
            received shouldContainExactly listOf("participation=PLAYER")
        }

        test("a campaign should be created from its body, answering its identifier") {
            api.createCampaign("Curse of Strahd", "Barovia", imageId).shouldBeSuccess() shouldBe campaignId
            received shouldContainExactly listOf(CampaignCreationRequestBody("Curse of Strahd", "Barovia", imageId))
        }

        test("a refusal should carry the message of the backend") {
            api.joinCampaign("ABC-DEF-GHJ-KLM").shouldBeFailure(BackendFailure.Rejected("campaign.invalid-invitation-code", "No such code."))
        }
    }
}
