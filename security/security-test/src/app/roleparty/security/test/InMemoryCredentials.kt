package app.roleparty.security.test

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.domain.model.Credential
import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.port.secondary.Credentials

internal class InMemoryCredentials : Credentials {

    private val credentials = mutableListOf<Credential>()

    override suspend fun saveIfAbsent(credential: Credential) {
        val absent = credentials.none { it.accountId == credential.accountId || it.emailAddress == credential.emailAddress }
        if (absent) credentials += credential
    }

    override suspend fun findByEmailAddress(emailAddress: EmailAddress) = credentials.find { it.emailAddress == emailAddress }

    override suspend fun findByAccountId(accountId: AccountId) = credentials.find { it.accountId == accountId }

    override suspend fun changeEmailAddress(accountId: AccountId, emailAddress: EmailAddress) =
        credentials.replaceAll { if (it.accountId == accountId) it.copy(emailAddress = emailAddress) else it }

    override suspend fun changePasswordHash(accountId: AccountId, passwordHash: PasswordHash) =
        credentials.replaceAll { if (it.accountId == accountId) it.copy(passwordHash = passwordHash) else it }
}
