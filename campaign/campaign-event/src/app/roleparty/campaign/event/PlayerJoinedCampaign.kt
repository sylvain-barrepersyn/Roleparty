package app.roleparty.campaign.event

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.rabbitmq.RabbitMqEvent
import kotlinx.serialization.Serializable

@Serializable
data class PlayerJoinedCampaign(
    val campaignId: CampaignId,
    val player: AccountId,
) : RabbitMqEvent {

    companion object {
        const val EXCHANGE = "campaign.player-joined-campaign"
    }
}
