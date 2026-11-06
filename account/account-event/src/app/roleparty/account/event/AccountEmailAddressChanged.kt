package app.roleparty.account.event

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.rabbitmq.RabbitMqEvent
import kotlinx.serialization.Serializable

@Serializable
data class AccountEmailAddressChanged(
    val accountId: AccountId,
    val emailAddress: EmailAddress,
) : RabbitMqEvent {

    companion object {
        const val EXCHANGE = "account.account-email-address-changed"
    }
}
