package app.roleparty.security.infrastructure

import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredEmailCodes
import app.roleparty.security.test.EmailCodesContractTest

class InfrastructuredEmailCodesContractTest : EmailCodesContractTest({
    TestPostgres.clear("email_code")
    InfrastructuredEmailCodes(TestPostgres.database)
})
