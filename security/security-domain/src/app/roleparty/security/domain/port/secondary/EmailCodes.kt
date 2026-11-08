package app.roleparty.security.domain.port.secondary

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.domain.model.EmailCode
import app.roleparty.security.domain.model.EmailCodePurpose
import app.roleparty.security.domain.model.SecretHash
import kotlin.time.Instant

interface EmailCodes {

    suspend fun save(code: EmailCode)

    suspend fun find(hash: SecretHash, purpose: EmailCodePurpose, emailAddress: EmailAddress): EmailCode?

    suspend fun deleteAll(purpose: EmailCodePurpose, emailAddress: EmailAddress)

    suspend fun deleteExpired(before: Instant)
}
