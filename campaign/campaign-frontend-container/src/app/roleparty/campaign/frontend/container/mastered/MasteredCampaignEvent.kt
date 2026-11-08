package app.roleparty.campaign.frontend.container.mastered

import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface MasteredCampaignEvent : ViewModelEvent {

    data object Exited : MasteredCampaignEvent
}
