package app.roleparty.account.contract.internal

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import kotlinx.rpc.annotations.Rpc

@Rpc
interface AccountRegisteringRpcService {

    suspend fun registerOrFind(emailAddress: EmailAddress): AccountId

    suspend fun ping()
}
