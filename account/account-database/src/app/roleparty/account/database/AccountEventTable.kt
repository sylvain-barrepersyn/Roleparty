package app.roleparty.account.database

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.communication.commons.EmailAddress
import kotlin.uuid.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.json.jsonb

object AccountEventTable : Table("$SCHEMA_ACCOUNT.event") {
    val accountId = uuid("account_id").transform(
        wrap = { AccountId.from(it.toString()) },
        unwrap = { Uuid.parse(it.toString()) },
    )
    val version = integer("version")
    val id = uuid("id")
    val occurredAt = timestamp("occurred_at")
    val payload = jsonb<StoredAccountEventPayload>("payload", Json)

    override val primaryKey = PrimaryKey(accountId, version)
}

@Serializable
sealed interface StoredAccountEventPayload {

    @Serializable
    @SerialName("registered")
    data class Registered(val emailAddress: EmailAddress, val role: AccountRole) : StoredAccountEventPayload

    @Serializable
    @SerialName("email-address-changed")
    data class EmailAddressChanged(val emailAddress: EmailAddress) : StoredAccountEventPayload

    @Serializable
    @SerialName("role-changed")
    data class RoleChanged(val role: AccountRole) : StoredAccountEventPayload
}
