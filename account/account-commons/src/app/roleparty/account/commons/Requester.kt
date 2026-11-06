package app.roleparty.account.commons

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Requester private constructor(val accountId: AccountId) {
    override fun toString() = accountId.toString()

    companion object {
        fun from(accountId: AccountId) = Requester(accountId)
    }
}
