package app.roleparty.account.commons

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@JvmInline
value class AccountId private constructor(private val value: Uuid): Comparable<AccountId> {
    override fun compareTo(other: AccountId) = value.compareTo(other.value)
    override fun toString() = value.toString()

    companion object {
        fun generate() = AccountId(Uuid.generateV7())
        fun from(value: String) = AccountId(Uuid.parse(value))
    }
}
