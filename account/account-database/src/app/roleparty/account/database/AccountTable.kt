package app.roleparty.account.database

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.communication.commons.EmailAddress
import org.jetbrains.exposed.v1.core.Table

object AccountTable : Table("$SCHEMA_ACCOUNT.account") {
    val id = uuid("id").transform(
        wrap = { AccountId.from(it) },
        unwrap = { it.toUuid() },
    )
    val emailAddress = text("email_address").transform(
        wrap = { EmailAddress.from(it) },
        unwrap = { it.toString() },
    )
    val role = text("role").transform(
        wrap = { AccountRole.valueOf(it) },
        unwrap = { it.name },
    )

    override val primaryKey = PrimaryKey(id)
}
