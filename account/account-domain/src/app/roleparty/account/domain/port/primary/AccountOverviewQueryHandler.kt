package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.communication.commons.EmailAddress

data class AccountOverviewQuery(
    val filter: Filter
) {
    sealed interface Filter {
        data class ByAccountId(val accountId: AccountId) : Filter
        data class ByEmailAddress(val emailAddress: EmailAddress) : Filter
    }
}

interface AccountOverviewQueryHandler {

    suspend fun handle(query: AccountOverviewQuery): AccountOverview?
}