package app.roleparty.campaign.tabletop.infrastructure.adapter.primary

import io.ktor.server.application.plugin
import io.ktor.server.resources.Resources
import io.ktor.server.resources.resource
import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.serializer

@PublishedApi
internal val PING_INTERVAL = 15.seconds

@PublishedApi
internal val TIMEOUT = 30.seconds

@PublishedApi
internal const val MAX_FRAME_BYTES = 256L * 1024

internal inline fun <reified T : Any> Route.webSocketResource(noinline handler: suspend DefaultWebSocketServerSession.(T) -> Unit) {
    resource<T> {
        webSocket {
            pingIntervalMillis = PING_INTERVAL.inWholeMilliseconds
            timeoutMillis = TIMEOUT.inWholeMilliseconds
            maxFrameSize = MAX_FRAME_BYTES
            val format = call.application.plugin(Resources).resourcesFormat
            val resource = try {
                format.decodeFromParameters(serializer<T>(), call.parameters)
            } catch (_: IllegalArgumentException) {
                return@webSocket close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unreadable parameters"))
            }
            handler(resource)
        }
    }
}
