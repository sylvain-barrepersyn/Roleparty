package app.roleparty.security.test

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.domain.model.EmailCode
import app.roleparty.security.domain.model.EmailCodePurpose
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.port.secondary.EmailCodes
import kotlin.time.Instant

internal class InMemoryEmailCodes : EmailCodes {

    private val codes = mutableListOf<EmailCode>()

    override suspend fun save(code: EmailCode) {
        codes += code
    }

    override suspend fun find(hash: SecretHash, purpose: EmailCodePurpose, emailAddress: EmailAddress) =
        codes.find { it.hash == hash && it.purpose == purpose && it.emailAddress == emailAddress }

    override suspend fun deleteAll(purpose: EmailCodePurpose, emailAddress: EmailAddress) {
        codes.removeIf { it.purpose == purpose && it.emailAddress == emailAddress }
    }

    override suspend fun deleteExpired(before: Instant) {
        codes.removeIf { it.expiration <= before }
    }

    fun count() = codes.size
}
