package app.roleparty.account.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.communication.commons.EmailAddress
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class AccountEvent(
    val id: Id,
    val accountId: AccountId,
    val occurredAt: Instant,
    val payload: Payload,
) {

    @JvmInline
    value class Id private constructor(private val value: Uuid) {
        override fun toString() = value.toString()

        companion object {
            fun generate() = Id(Uuid.generateV7())
            fun from(value: String) = Id(Uuid.parse(value))
        }
    }

    sealed interface Payload {

        data class Registered(
            val emailAddress: EmailAddress,
            val role: AccountRole,
        ) : Payload

        data class EmailAddressChanged(val emailAddress: EmailAddress) : Payload

        data class RoleChanged(val role: AccountRole) : Payload
    }
}
