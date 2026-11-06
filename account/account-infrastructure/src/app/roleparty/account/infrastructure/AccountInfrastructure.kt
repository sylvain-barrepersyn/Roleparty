package app.roleparty.account.infrastructure

import app.roleparty.account.contract.rpc.AccountRpcService
import app.roleparty.account.database.setUpAccountSchema
import app.roleparty.account.domain.AccountModule
import app.roleparty.account.infrastructure.adapter.primary.AccountRpcController
import app.roleparty.account.infrastructure.adapter.secondary.InfrastructuredAccounts
import app.roleparty.core.commons.requireValueOrFile
import app.roleparty.core.database.Postgres
import app.roleparty.core.krpc.serveRpc
import io.ktor.server.application.Application
import kotlin.time.Clock

private const val RPC_TOKEN = "ACCOUNT_RPC_TOKEN"
private const val RPC_PATH = "/rpc/account"

fun loadAccountModule(postgres: Postgres, clock: Clock = Clock.System): AccountModule {
    postgres.setUpAccountSchema()

    val accounts = InfrastructuredAccounts(postgres.database)
    return AccountModule(accounts = accounts, accountOverviews = accounts, clock = clock)
}

fun Application.serveAccountRpc(module: AccountModule, env: Map<String, String> = System.getenv()) {
    val controller = AccountRpcController(module.accountRegistrationCommandHandler, module.accountOverviewQueryHandler)

    serveRpc(RPC_PATH, env.requireValueOrFile(RPC_TOKEN)) {
        registerService<AccountRpcService> { controller }
    }
}
