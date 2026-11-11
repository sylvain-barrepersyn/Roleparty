package app.roleparty.campaign.frontend.container.selection

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface CampaignSelectionAction : ViewModelAction {

    data class ShowParticipation(val participation: Participation) : CampaignSelectionAction

    data object Refresh : CampaignSelectionAction

    data class OpenCampaign(val campaignId: CampaignId) : CampaignSelectionAction

    data object CreateCampaign : CampaignSelectionAction

    data object JoinCampaign : CampaignSelectionAction
}
