package app.roleparty.security.infrastructure

import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredRefreshTokens
import app.roleparty.security.test.RefreshTokensContractTest

class InfrastructuredRefreshTokensContractTest : RefreshTokensContractTest({
    TestPostgres.clear("refresh_token")
    InfrastructuredRefreshTokens(TestPostgres.database)
})
