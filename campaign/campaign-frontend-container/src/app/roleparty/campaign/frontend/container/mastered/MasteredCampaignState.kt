package app.roleparty.campaign.frontend.container.mastered

import app.roleparty.campaign.contract.web.CampaignResponseBody
import app.roleparty.campaign.frontend.component.mastered.MasteredCampaignSection
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface MasteredCampaignState : ViewModelState {

    data object Loading : MasteredCampaignState

    data class Loaded(val campaign: CampaignResponseBody, val section: MasteredCampaignSection = MasteredCampaignSection.SCENES) : MasteredCampaignState

    data class Failed(val failure: BackendFailure) : MasteredCampaignState
}
