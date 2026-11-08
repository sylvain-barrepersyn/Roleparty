package app.roleparty.security.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.SecurityError

interface ChangePassword {

    suspend operator fun invoke(accountId: AccountId, currentPassword: String, newPassword: String): Outcome<Unit, SecurityError>
}
