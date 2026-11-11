package app.roleparty.campaign.frontend.container.selection

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface CampaignSelectionEvent : ViewModelEvent {

    data class MasteredCampaignOpened(val campaignId: CampaignId) : CampaignSelectionEvent

    data class PlayedCampaignOpened(val campaignId: CampaignId) : CampaignSelectionEvent

    data object CampaignCreationRequested : CampaignSelectionEvent

    data object CampaignJoiningRequested : CampaignSelectionEvent
}
