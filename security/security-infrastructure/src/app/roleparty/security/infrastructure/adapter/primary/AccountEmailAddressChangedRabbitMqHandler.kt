package app.roleparty.security.infrastructure.adapter.primary

import app.roleparty.account.event.AccountEmailAddressChanged
import app.roleparty.core.rabbitmq.RabbitMqEventHandler
import app.roleparty.security.domain.port.primary.ChangeCredentialEmailAddress
import kotlin.uuid.Uuid

class AccountEmailAddressChangedRabbitMqHandler internal constructor(
    private val changeCredentialEmailAddress: ChangeCredentialEmailAddress,
) : RabbitMqEventHandler<AccountEmailAddressChanged> {

    override suspend fun handle(eventId: Uuid, event: AccountEmailAddressChanged) {
        changeCredentialEmailAddress(event.accountId, event.emailAddress)
    }

    companion object {
        const val QUEUE = "security.account.account-email-address-changed"
    }
}
