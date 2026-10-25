package app.roleparty.account.commons

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@JvmInline
value class AccountId private constructor(private val value: String): Comparable<AccountId> {
    override fun compareTo(other: AccountId) = value.compareTo(other.value)

    companion object {
        fun generate() = AccountId(Uuid.generateV7().toString())
        fun fromString(value: String) = AccountId(value)
    }
}