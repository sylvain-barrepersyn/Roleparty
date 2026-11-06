package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.AccountError
import app.roleparty.core.commons.Outcome

data class AccountRoleChangeCommand(
    val requester: Requester,
    val role: AccountRole,
)

interface AccountRoleChangeCommandHandler {

    suspend fun handle(accountId: AccountId, command: AccountRoleChangeCommand): Outcome<Unit, AccountError>
}
