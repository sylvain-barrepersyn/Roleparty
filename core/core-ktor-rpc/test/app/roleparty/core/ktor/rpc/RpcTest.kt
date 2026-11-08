package app.roleparty.core.ktor.rpc

import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.net.ServerSocket
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.ktor.server.KrpcRoute
import kotlinx.rpc.withService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Rpc
interface GreetingSendingService {
    suspend fun greet(name: String): String
}

class RpcTest {

    private val token = "a-token-long-enough-to-be-accepted"
    private val port = ServerSocket(0).use { it.localPort }
    private val url = "ws://localhost:$port/rpc/greeting"

    private val server: EmbeddedServer<*, *> = embeddedServer(Netty, port = port) {
        serveRpc("/rpc/greeting", token) { registerGreeting() }
    }.start(wait = false)

    private fun KrpcRoute.registerGreeting() {
        registerService<GreetingSendingService> {
            object : GreetingSendingService {
                override suspend fun greet(name: String) = "Hello $name"
            }
        }
    }

    private fun connection(token: String) = server.application.rpcConnection(url, token)

    @AfterEach
    fun stop() {
        server.stop(0, 0)
    }

    @Test
    fun `should call a service with the right token, on a new connection each time`() = runBlocking {
        // Given
        val connection = connection(token)

        // When
        val first = connection.call { it.withService<GreetingSendingService>().greet("Gimli") }
        val second = connection.call { it.withService<GreetingSendingService>().greet("Legolas") }

        // Then
        assertEquals("Hello Gimli", first)
        assertEquals("Hello Legolas", second)
    }

    @Test
    fun `should refuse a call with a wrong token`() = runBlocking<Unit> {
        // Given
        val connection = connection("a-wrong-token-of-a-valid-length-too")

        // When / Then
        assertThrows<Exception> { connection.call { it.withService<GreetingSendingService>().greet("Gimli") } }
    }

    @Test
    fun `should refuse to serve with a token too short to be safe`() {
        assertThrows<IllegalStateException> {
            embeddedServer(Netty, port = 0) { serveRpc("/rpc/greeting", "short") { registerGreeting() } }.start(wait = false)
        }
    }
}
