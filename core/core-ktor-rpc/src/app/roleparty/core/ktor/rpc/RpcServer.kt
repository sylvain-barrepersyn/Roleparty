package app.roleparty.core.ktor.rpc

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.security.MessageDigest
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.KrpcRoute
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json

private const val MIN_TOKEN_LENGTH = 32

private class RpcTokenConfiguration {
    var token: String = ""
}

private val RpcToken = createRouteScopedPlugin("RpcToken", ::RpcTokenConfiguration) {
    val expected = "Bearer ${pluginConfig.token}".toByteArray()

    onCall { call ->
        val provided = call.request.header(HttpHeaders.Authorization).orEmpty().toByteArray()
        if (!MessageDigest.isEqual(provided, expected)) call.respond(HttpStatusCode.Unauthorized)
    }
}

fun Application.serveRpc(path: String, token: String, services: KrpcRoute.() -> Unit) {
    check(token.length >= MIN_TOKEN_LENGTH) { "The RPC token of $path must be at least $MIN_TOKEN_LENGTH characters long" }

    pluginOrNull(Krpc) ?: install(Krpc)

    routing {
        route(path) {
            install(RpcToken) { this.token = token }

            rpc {
                rpcConfig {
                    serialization { json() }
                }
                services()
            }
        }
    }
}
