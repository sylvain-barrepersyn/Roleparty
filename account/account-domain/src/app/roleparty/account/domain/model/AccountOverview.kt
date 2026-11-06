package app.roleparty.account.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.model.AccountEvent.Payload.EmailAddressChanged
import app.roleparty.account.domain.model.AccountEvent.Payload.Registered
import app.roleparty.account.domain.model.AccountEvent.Payload.RoleChanged
import app.roleparty.communication.commons.EmailAddress

@ConsistentCopyVisibility
data class AccountOverview private constructor(
    val accountId: AccountId,
    val emailAddress: EmailAddress,
    val role: AccountRole,
) {
    val isAdmin get() = role == AccountRole.ADMIN

    private fun apply(payload: Payload) = when (payload) {
        is Registered -> error("Account $accountId is already registered")
        is EmailAddressChanged -> copy(emailAddress = payload.emailAddress)
        is RoleChanged -> copy(role = payload.role)
    }

    companion object {
        fun from(events: List<AccountEvent>): AccountOverview {
            val registration = events.firstOrNull()
            val payload = registration?.payload
            check(payload is Registered) { "Cannot create an overview from a non registered account" }

            val initial = AccountOverview(registration.accountId, payload.emailAddress, payload.role)
            return events.drop(1).fold(initial) { overview, event -> overview.apply(event.payload) }
        }
    }
}
