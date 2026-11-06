package app.roleparty.account.test

import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.account.domain.port.secondary.AccountOverviews
import app.roleparty.communication.commons.EmailAddress

internal class RacingAccountOverviews(private val delegate: AccountOverviews) : AccountOverviews by delegate {

    var emailCheckMissesConcurrentWrites = false

    override suspend fun findByEmailAddress(emailAddress: EmailAddress): AccountOverview? =
        if (emailCheckMissesConcurrentWrites) null else delegate.findByEmailAddress(emailAddress)
}
