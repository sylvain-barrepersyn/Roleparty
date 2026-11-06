package app.roleparty.account.database

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.communication.commons.EmailAddress
import kotlin.uuid.Uuid
import org.jetbrains.exposed.v1.core.Table

object AccountOverviewTable : Table("$SCHEMA_ACCOUNT.overview") {
    val accountId = uuid("account_id").transform(
        wrap = { AccountId.from(it.toString()) },
        unwrap = { Uuid.parse(it.toString()) },
    )
    val emailAddress = text("email_address").transform(
        wrap = { EmailAddress.from(it) },
        unwrap = { it.toString() },
    )
    val role = text("role").transform(
        wrap = { AccountRole.valueOf(it) },
        unwrap = { it.name },
    )

    override val primaryKey = PrimaryKey(accountId)
}
