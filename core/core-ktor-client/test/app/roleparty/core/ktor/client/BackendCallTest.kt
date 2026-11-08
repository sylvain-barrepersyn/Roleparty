package app.roleparty.core.ktor.client

import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.resources.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.net.ServerSocket
import kotlinx.serialization.Serializable

@Serializable
@Resource("/things/{name}")
private class Thing(val name: String)

@Serializable
private data class ThingBody(val name: String, val size: Int)

class BackendCallTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val port = ServerSocket(0).use { it.localPort }
    private var acceptedLanguage: String? = null

    private val server = embeddedServer(Netty, port = port) {
        routing {
            get("/things/{name}") {
                acceptedLanguage = call.request.headers[HttpHeaders.AcceptLanguage]
                when (val name = call.parameters["name"]) {
                    "nothing" -> call.respond(HttpStatusCode.NoContent)
                    "refused" -> call.respondText("""{"code":"thing.refused","message":"No way."}""", ContentType.Application.Json, HttpStatusCode.Conflict)
                    "broken" -> call.respondText("oops", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    else -> call.respondText("""{"name":"$name","size":3,"extra":true}""", ContentType.Application.Json)
                }
            }
        }
    }.start(wait = false)

    private val client = HttpClient(CIO) { installBackend("http://localhost:$port") }

    init {
        afterTest {
            client.close()
            server.stop(0, 0)
        }

        test("a successful answer should be read from the resource, ignoring unknown fields") {
            backendCall<ThingBody> { client.get(Thing("lamp")) }.shouldBeSuccess() shouldBe ThingBody("lamp", 3)
            acceptedLanguage shouldBe java.util.Locale.getDefault().language
        }

        test("an answer without body should be a success when nothing is expected") {
            backendCall<Unit> { client.get(Thing("nothing")) }.shouldBeSuccess()
        }

        test("a refusal should carry the code and message of the backend") {
            backendCall<ThingBody> { client.get(Thing("refused")) }.shouldBeFailure(BackendFailure.Rejected("thing.refused", "No way."))
        }

        test("an answer that can't be read should be unexpected") {
            backendCall<ThingBody> { client.get(Thing("broken")) }.shouldBeFailure(BackendFailure.Unexpected)
        }

        test("a backend that can't be reached should be unreachable") {
            val offline = HttpClient(CIO) { installBackend("http://localhost:${ServerSocket(0).use { it.localPort }}") }

            backendCall<ThingBody> { offline.get(Thing("lamp")) }.shouldBeFailure(BackendFailure.Unreachable)
            offline.close()
        }
    }
}
