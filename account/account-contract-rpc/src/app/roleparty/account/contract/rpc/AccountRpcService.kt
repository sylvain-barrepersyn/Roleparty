package app.roleparty.account.contract.rpc

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import kotlinx.rpc.annotations.Rpc

@Rpc
interface AccountRpcService {

    suspend fun registerOrFind(emailAddress: EmailAddress): AccountId
}
