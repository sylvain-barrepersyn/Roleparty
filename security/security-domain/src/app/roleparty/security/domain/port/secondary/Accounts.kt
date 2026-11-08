package app.roleparty.security.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress

interface Accounts {

    suspend fun registerOrFind(emailAddress: EmailAddress): AccountId
}
