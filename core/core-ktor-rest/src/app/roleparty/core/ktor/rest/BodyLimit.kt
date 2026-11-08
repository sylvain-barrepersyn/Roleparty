package app.roleparty.core.ktor.rest

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.request.contentLength
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.respond
import io.ktor.utils.io.readBuffer
import kotlinx.io.readByteArray

class BodyLimitConfiguration {
    var maxBytes: Long = 0
}

val BodyLimit = createRouteScopedPlugin("BodyLimit", ::BodyLimitConfiguration) {
    val maxBytes = pluginConfig.maxBytes

    onCall { call ->
        val length = call.request.contentLength()
        when {
            length == null -> call.respond(HttpStatusCode.LengthRequired)
            length > maxBytes -> call.respond(HttpStatusCode.PayloadTooLarge)
        }
    }
}

suspend fun ApplicationCall.receiveBytes(maxBytes: Long): ByteArray? {
    val length = request.contentLength()
    if (length != null && length > maxBytes) return null

    val content = receiveChannel().readBuffer(maxBytes + 1).readByteArray()
    return content.takeIf { it.size <= maxBytes }
}
