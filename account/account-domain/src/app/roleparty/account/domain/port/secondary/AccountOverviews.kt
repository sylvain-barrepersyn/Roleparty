package app.roleparty.account.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.communication.commons.EmailAddress

interface AccountOverviews {

    suspend fun findById(accountId: AccountId): AccountOverview?

    suspend fun findByEmailAddress(emailAddress: EmailAddress): AccountOverview?
}
