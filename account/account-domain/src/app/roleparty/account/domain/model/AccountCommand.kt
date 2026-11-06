package app.roleparty.account.domain.model

import app.roleparty.account.commons.AccountRole
import app.roleparty.communication.commons.EmailAddress

sealed interface AccountCreationCommand {

    data class Register(val emailAddress: EmailAddress) : AccountCreationCommand
}

sealed interface AccountLifecycleCommand {

    data class ChangeEmailAddress(val emailAddress: EmailAddress) : AccountLifecycleCommand

    data class ChangeRole(val role: AccountRole) : AccountLifecycleCommand
}
