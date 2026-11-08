package app.roleparty.campaign.frontend.container.joining

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface CampaignJoiningEvent : ViewModelEvent {

    data class CampaignJoined(val campaignId: CampaignId) : CampaignJoiningEvent

    data object JoiningCancelled : CampaignJoiningEvent
}
