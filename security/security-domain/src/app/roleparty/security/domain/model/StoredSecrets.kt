package app.roleparty.security.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import kotlin.time.Instant

data class Credential(
    val accountId: AccountId,
    val emailAddress: EmailAddress,
    val passwordHash: PasswordHash,
)

data class StoredRefreshToken(
    val hash: SecretHash,
    val accountId: AccountId,
    val deviceId: DeviceId,
    val expiration: Instant,
    val rotatedAt: Instant?,
    val revoked: Boolean,
)

enum class EmailCodePurpose {
    SIGN_UP,
    PASSWORD_RESET,
}

data class EmailCode(
    val hash: SecretHash,
    val purpose: EmailCodePurpose,
    val emailAddress: EmailAddress,
    val passwordHash: PasswordHash?,
    val expiration: Instant,
)
