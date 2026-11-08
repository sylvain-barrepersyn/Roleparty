package app.roleparty.campaign.event

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.rabbitmq.RabbitMqEvent
import kotlinx.serialization.Serializable

@Serializable
data class CampaignCreated(
    val campaignId: CampaignId,
    val gameMaster: AccountId,
) : RabbitMqEvent {

    companion object {
        const val EXCHANGE = "campaign.campaign-created"
    }
}
