package app.roleparty.campaign.frontend.container.settings

import app.roleparty.core.frontend.container.ViewModelAction

sealed interface CampaignSettingsAction : ViewModelAction {

    data object Retry : CampaignSettingsAction

    data object GenerateInvitation : CampaignSettingsAction
}
