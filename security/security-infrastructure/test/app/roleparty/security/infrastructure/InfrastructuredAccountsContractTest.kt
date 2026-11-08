package app.roleparty.security.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.account.contract.internal.AccountRegisteringRpcService
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.ktor.rpc.rpcConnection
import app.roleparty.core.ktor.rpc.serveRpc
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredAccounts
import app.roleparty.security.test.AccountsContractTest
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap

private object TestAccountRpc {
    private const val TOKEN = "a-token-long-enough-to-be-accepted"
    private val port = ServerSocket(0).use { it.localPort }
    private val accounts = ConcurrentHashMap<EmailAddress, AccountId>()

    private val server = embeddedServer(Netty, port = port) {
        serveRpc("/rpc/account", TOKEN) {
            registerService<AccountRegisteringRpcService> {
                object : AccountRegisteringRpcService {
                    override suspend fun registerOrFind(emailAddress: EmailAddress) =
                        accounts.getOrPut(emailAddress) { AccountId.generate() }

                    override suspend fun ping() = Unit
                }
            }
        }
    }.start(wait = false)

    fun connection() = server.application.rpcConnection("ws://localhost:$port/rpc/account", TOKEN)
}

class InfrastructuredAccountsContractTest : AccountsContractTest({
    InfrastructuredAccounts(TestAccountRpc.connection())
})
