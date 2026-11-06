package app.roleparty.account.infrastructure

import app.roleparty.account.infrastructure.adapter.secondary.InfrastructuredAccounts
import app.roleparty.account.test.AccountsContractSubject
import app.roleparty.account.test.AccountsContractTest
import kotlinx.coroutines.runBlocking

class InfrastructuredAccountsContractTest : AccountsContractTest({
    runBlocking { TestPostgres.clear() }
    val store = InfrastructuredAccounts(TestPostgres.postgres.database)
    AccountsContractSubject(accounts = store, accountOverviews = store)
})
