package app.roleparty.campaign.frontend.container.creation

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface CampaignCreationEvent : ViewModelEvent {

    data class CampaignCreated(val campaignId: CampaignId) : CampaignCreationEvent

    data object CreationCancelled : CampaignCreationEvent
}
