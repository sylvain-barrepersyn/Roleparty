package app.roleparty.core.ktor.rpc

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json

class RpcConnection internal constructor(
    private val httpClient: HttpClient,
    private val url: String,
    private val token: String,
) {

    suspend fun <T> call(block: suspend (RpcClient) -> T): T {
        val client = httpClient.rpc(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
            rpcConfig {
                serialization { json() }
            }
        }
        return try {
            block(client)
        } finally {
            client.close()
        }
    }
}

fun Application.rpcConnection(url: String, token: String): RpcConnection {
    val httpClient = HttpClient(CIO) { installKrpc() }
    monitor.subscribe(ApplicationStopped) { httpClient.close() }

    return RpcConnection(httpClient, url, token)
}
