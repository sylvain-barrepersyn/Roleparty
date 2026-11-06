package app.roleparty.account.test

class InMemoryAccountsTest : AccountsContractTest({
    val store = InMemoryAccounts()
    AccountsContractSubject(accounts = store, accountOverviews = store)
})
