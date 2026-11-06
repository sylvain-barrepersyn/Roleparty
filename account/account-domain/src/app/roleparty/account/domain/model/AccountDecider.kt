package app.roleparty.account.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountCreationCommand.Register
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.model.AccountEvent.Payload.EmailAddressChanged
import app.roleparty.account.domain.model.AccountEvent.Payload.Registered
import app.roleparty.account.domain.model.AccountEvent.Payload.RoleChanged
import app.roleparty.account.domain.model.AccountLifecycleCommand.ChangeEmailAddress
import app.roleparty.account.domain.model.AccountLifecycleCommand.ChangeRole
import app.roleparty.communication.commons.EmailAddress

class AccountDecider private constructor(private val decisionProjection: DecisionProjection) {

    fun decide(command: AccountLifecycleCommand): List<Payload> = when (command) {
        is ChangeEmailAddress ->
            if (command.emailAddress == decisionProjection.emailAddress) emptyList()
            else listOf(EmailAddressChanged(command.emailAddress))

        is ChangeRole ->
            if (command.role == decisionProjection.role) emptyList()
            else listOf(RoleChanged(command.role))
    }

    companion object {
        fun from(events: List<AccountEvent>) = AccountDecider(DecisionProjection.from(events))

        fun decide(command: AccountCreationCommand): List<Payload> = when (command) {
            is Register -> listOf(Registered(emailAddress = command.emailAddress, role = AccountRole.USER))
        }
    }

    @ConsistentCopyVisibility
    private data class DecisionProjection private constructor(
        val accountId: AccountId,
        val emailAddress: EmailAddress,
        val role: AccountRole,
    ) {
        fun apply(payload: Payload) = when (payload) {
            is Registered -> error("Account $accountId is already registered")
            is EmailAddressChanged -> copy(emailAddress = payload.emailAddress)
            is RoleChanged -> copy(role = payload.role)
        }

        companion object {
            fun from(events: List<AccountEvent>): DecisionProjection {
                val registration = events.firstOrNull()
                val payload = registration?.payload
                check(payload is Registered) { "An account event stream must start with a registration" }

                val initial = DecisionProjection(registration.accountId, payload.emailAddress, payload.role)
                return events.drop(1).fold(initial) { projection, event -> projection.apply(event.payload) }
            }
        }
    }
}
