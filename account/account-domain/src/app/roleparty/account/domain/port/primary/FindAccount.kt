package app.roleparty.account.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.Account
import app.roleparty.communication.commons.EmailAddress

interface FindAccount {

    suspend fun byId(accountId: AccountId): Account?

    suspend fun byEmailAddress(emailAddress: EmailAddress): Account?
}
