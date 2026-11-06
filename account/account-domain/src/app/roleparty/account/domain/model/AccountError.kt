package app.roleparty.account.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress

sealed interface AccountError {

    data class EmailAddressAlreadyUsed(val emailAddress: EmailAddress) : AccountError

    data class AccountNotFound(val accountId: AccountId) : AccountError

    data class ConcurrentModification(val accountId: AccountId) : AccountError

    data object AdminRoleRequired : AccountError

    data object OwnRoleChangeForbidden : AccountError
}
