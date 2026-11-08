package app.roleparty.security.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.domain.model.Credential
import app.roleparty.security.domain.model.PasswordHash

interface Credentials {

    suspend fun saveIfAbsent(credential: Credential)

    suspend fun findByEmailAddress(emailAddress: EmailAddress): Credential?

    suspend fun findByAccountId(accountId: AccountId): Credential?

    suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress)

    suspend fun changePasswordHash(accountId: AccountId, passwordHash: PasswordHash)
}
