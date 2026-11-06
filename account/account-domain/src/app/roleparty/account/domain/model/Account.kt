package app.roleparty.account.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.communication.commons.EmailAddress

data class Account(
    val id: AccountId,
    val emailAddress: EmailAddress,
    val role: AccountRole,
) {
    val isAdmin get() = role == AccountRole.ADMIN
}
