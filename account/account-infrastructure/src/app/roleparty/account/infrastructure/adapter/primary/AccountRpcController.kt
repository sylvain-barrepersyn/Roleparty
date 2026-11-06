package app.roleparty.account.infrastructure.adapter.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.contract.rpc.AccountRpcService
import app.roleparty.account.domain.port.primary.AccountUseCases
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.getOrElse

internal class AccountRpcController(private val useCases: AccountUseCases) : AccountRpcService {

    override suspend fun registerOrFind(emailAddress: EmailAddress): AccountId =
        useCases.register(emailAddress).getOrElse {
            useCases.findByEmailAddress(emailAddress)?.id ?: error("Impossible to register an account: $it")
        }
}
