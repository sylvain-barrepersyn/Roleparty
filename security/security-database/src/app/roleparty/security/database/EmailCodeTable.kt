package app.roleparty.security.database

import app.roleparty.communication.commons.EmailAddress
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

object EmailCodeTable : Table("$SCHEMA_SECURITY.email_code") {
    val codeHash = text("code_hash")
    val purpose = text("purpose")
    val emailAddress = text("email_address").transform(
        wrap = { EmailAddress.from(it) },
        unwrap = { it.toString() },
    )
    val passwordHash = text("password_hash").nullable()
    val expiration = timestamp("expiration")

    override val primaryKey = PrimaryKey(codeHash, purpose, emailAddress)
}
