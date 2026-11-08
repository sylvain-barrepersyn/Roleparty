package app.roleparty.security.infrastructure.adapter.secondary

import app.roleparty.account.contract.internal.AccountRegisteringRpcService
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.ktor.rpc.RpcConnection
import app.roleparty.security.domain.port.secondary.Accounts
import kotlinx.rpc.withService

internal class InfrastructuredAccounts(private val connection: RpcConnection) : Accounts {

    override suspend fun registerOrFind(emailAddress: EmailAddress) =
        connection.call { it.withService<AccountRegisteringRpcService>().registerOrFind(emailAddress) }

    suspend fun ping() = connection.call { it.withService<AccountRegisteringRpcService>().ping() }
}
