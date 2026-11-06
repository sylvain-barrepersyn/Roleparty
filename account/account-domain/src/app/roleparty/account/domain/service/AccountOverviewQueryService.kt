package app.roleparty.account.domain.service

import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.account.domain.port.primary.AccountOverviewQuery
import app.roleparty.account.domain.port.primary.AccountOverviewQuery.Filter
import app.roleparty.account.domain.port.primary.AccountOverviewQueryHandler
import app.roleparty.account.domain.port.secondary.AccountOverviews

internal class AccountOverviewQueryService(
    private val accountOverviews: AccountOverviews,
) : AccountOverviewQueryHandler {

    override suspend fun handle(query: AccountOverviewQuery): AccountOverview? = when (val filter = query.filter) {
        is Filter.ByAccountId -> accountOverviews.findById(filter.accountId)
        is Filter.ByEmailAddress -> accountOverviews.findByEmailAddress(filter.emailAddress)
    }
}
