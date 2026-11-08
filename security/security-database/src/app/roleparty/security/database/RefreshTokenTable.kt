package app.roleparty.security.database

import app.roleparty.account.commons.AccountId
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

object RefreshTokenTable : Table("$SCHEMA_SECURITY.refresh_token") {
    val tokenHash = text("token_hash")
    val accountId = uuid("account_id").transform(
        wrap = { AccountId.from(it) },
        unwrap = { it.toUuid() },
    )
    val deviceId = uuid("device_id")
    val expiration = timestamp("expiration")
    val rotatedAt = timestamp("rotated_at").nullable()
    val revoked = bool("revoked")

    override val primaryKey = PrimaryKey(tokenHash)
}
