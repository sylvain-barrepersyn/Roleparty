package app.roleparty.security.infrastructure

import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredCredentials
import app.roleparty.security.test.CredentialsContractTest

class InfrastructuredCredentialsContractTest : CredentialsContractTest({
    TestPostgres.clear("credential")
    InfrastructuredCredentials(TestPostgres.database)
})
