package app.roleparty.account.infrastructure.adapter.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.contract.internal.AccountRegisteringRpcService
import app.roleparty.account.domain.port.primary.FindAccount
import app.roleparty.account.domain.port.primary.RegisterAccount
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.getOrElse

internal class AccountRpcController(
    private val registerAccount: RegisterAccount,
    private val findAccount: FindAccount,
) : AccountRegisteringRpcService {

    override suspend fun registerOrFind(emailAddress: EmailAddress): AccountId =
        registerAccount(emailAddress).getOrElse {
            findAccount.byEmailAddress(emailAddress)?.id ?: error("Impossible to register an account: $it")
        }

    override suspend fun ping() = Unit
}
