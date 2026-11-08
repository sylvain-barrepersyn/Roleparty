package app.roleparty.security.database

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import org.jetbrains.exposed.v1.core.Table

object CredentialTable : Table("$SCHEMA_SECURITY.credential") {
    val accountId = uuid("account_id").transform(
        wrap = { AccountId.from(it) },
        unwrap = { it.toUuid() },
    )
    val emailAddress = text("email_address").transform(
        wrap = { EmailAddress.from(it) },
        unwrap = { it.toString() },
    )
    val passwordHash = text("password_hash")

    override val primaryKey = PrimaryKey(accountId)
}
