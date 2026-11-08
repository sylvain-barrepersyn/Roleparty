package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.AccountError
import app.roleparty.core.commons.Outcome

interface ChangeAccountRole {

    suspend operator fun invoke(requester: Requester, accountId: AccountId, role: AccountRole): Outcome<Unit, AccountError>
}
