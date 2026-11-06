package app.roleparty.account.infrastructure

import app.roleparty.account.infrastructure.adapter.secondary.InfrastructuredAccounts
import app.roleparty.account.test.AccountsContractTest
import kotlin.time.Clock
import kotlinx.coroutines.runBlocking

class InfrastructuredAccountsContractTest : AccountsContractTest({
    runBlocking { TestPostgres.clear() }
    InfrastructuredAccounts(TestPostgres.postgres.database, Clock.System)
})
