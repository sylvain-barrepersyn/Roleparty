package app.roleparty.media.frontend.container

import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.container.data.HttpMediaApi
import app.roleparty.media.frontend.container.data.MediaFailure
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.contentType
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.net.ServerSocket

class HttpMediaApiTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val port = ServerSocket(0).use { it.localPort }
    private val imageId = ImageId.generate()
    private var status = HttpStatusCode.Created
    private var body = """{"id":"$imageId"}"""
    private var received: Pair<ContentType, ByteArray>? = null

    private val server = embeddedServer(Netty, port = port) {
        routing {
            post("/v1/images") {
                received = call.request.contentType() to call.receive<ByteArray>()
                call.respondText(body, ContentType.Application.Json, status)
            }
        }
    }.start(wait = false)

    private val client = HttpClient(CIO) { expectSuccess = false }
    private val api = HttpMediaApi(client, "http://localhost:$port")
    private val content = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())

    init {
        afterTest {
            client.close()
            server.stop(0, 0)
        }

        test("should send the image with its media type and read its identifier") {
            api.upload(content, ImageFormat.JPEG).shouldBeSuccess() shouldBe imageId

            val request = received!!
            request.first shouldBe ContentType.Image.JPEG
            request.second.contentEquals(content) shouldBe true
        }

        test("should read the code and the message of a refusal") {
            status = HttpStatusCode.BadRequest
            body = """{"code":"media.invalid-image","message":"Not an image."}"""

            api.upload(content, ImageFormat.JPEG) shouldBeFailure MediaFailure.Rejected("media.invalid-image", "Not an image.")
        }

        test("should treat a refusal without details as unexpected") {
            status = HttpStatusCode.Unauthorized
            body = ""

            api.upload(content, ImageFormat.JPEG) shouldBeFailure MediaFailure.Unexpected
        }

        test("should report an unreachable backend") {
            val closedPort = ServerSocket(0).use { it.localPort }

            HttpMediaApi(client, "http://localhost:$closedPort").upload(content, ImageFormat.JPEG) shouldBeFailure MediaFailure.Unreachable
        }

        test("should build the address of an image") {
            api.imageUrl(imageId) shouldBe "http://localhost:$port/v1/images/$imageId"
        }
    }
}
