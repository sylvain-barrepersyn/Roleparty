package app.roleparty.campaign.frontend.container.selection

import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface CampaignSelectionState : ViewModelState {

    val participation: Participation

    data class Loading(override val participation: Participation) : CampaignSelectionState

    data class Loaded(override val participation: Participation, val campaigns: List<CampaignOverviewResponseBody>) : CampaignSelectionState

    data class Failed(override val participation: Participation, val failure: BackendFailure) : CampaignSelectionState
}
