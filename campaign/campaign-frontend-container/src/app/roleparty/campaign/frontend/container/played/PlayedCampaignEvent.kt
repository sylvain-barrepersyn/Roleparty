package app.roleparty.campaign.frontend.container.played

import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface PlayedCampaignEvent : ViewModelEvent {

    data object Exited : PlayedCampaignEvent
}
