package app.roleparty.campaign.frontend.container.played

import app.roleparty.campaign.frontend.component.played.PlayedCampaignSection
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface PlayedCampaignAction : ViewModelAction {

    data object Retry : PlayedCampaignAction

    data class ShowSection(val section: PlayedCampaignSection) : PlayedCampaignAction

    data object Exit : PlayedCampaignAction
}
