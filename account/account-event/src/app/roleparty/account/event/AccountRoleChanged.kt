package app.roleparty.account.event

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.core.rabbitmq.RabbitMqEvent
import kotlinx.serialization.Serializable

@Serializable
data class AccountRoleChanged(
    val accountId: AccountId,
    val role: AccountRole,
) : RabbitMqEvent {

    companion object {
        const val EXCHANGE = "account.account-role-changed"
    }
}
