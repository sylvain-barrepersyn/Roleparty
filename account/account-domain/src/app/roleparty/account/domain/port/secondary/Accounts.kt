package app.roleparty.account.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountHistory
import app.roleparty.account.domain.model.AccountVersion
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome

interface Accounts {

    suspend fun append(
        accountId: AccountId,
        expectedVersion: AccountVersion,
        events: List<AccountEvent>,
    ): Outcome<Unit, AccountAppendFailure>

    suspend fun load(accountId: AccountId): AccountHistory
}

sealed interface AccountAppendFailure {

    data object VersionConflict : AccountAppendFailure

    data class EmailAddressAlreadyUsed(val emailAddress: EmailAddress) : AccountAppendFailure
}
