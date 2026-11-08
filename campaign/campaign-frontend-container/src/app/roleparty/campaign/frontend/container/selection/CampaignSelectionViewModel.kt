package app.roleparty.campaign.frontend.container.selection

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CampaignSelectionViewModel(private val api: CampaignApi) :
    ViewModel<CampaignSelectionState, CampaignSelectionAction, CampaignSelectionEvent>(CampaignSelectionState.Loading(Participation.GAME_MASTER)) {

    private var loading: Job? = null

    override fun handle(action: CampaignSelectionAction) {
        when (action) {
            is CampaignSelectionAction.ShowParticipation -> if (action.participation != state.value.participation) load(action.participation)
            CampaignSelectionAction.Refresh -> load(state.value.participation)
            is CampaignSelectionAction.OpenMasteredCampaign -> emit(CampaignSelectionEvent.MasteredCampaignOpened(action.campaignId))
            CampaignSelectionAction.CreateCampaign -> emit(CampaignSelectionEvent.CampaignCreationRequested)
            CampaignSelectionAction.JoinCampaign -> emit(CampaignSelectionEvent.CampaignJoiningRequested)
        }
    }

    private fun load(participation: Participation) {
        loading?.cancel()
        update { CampaignSelectionState.Loading(participation) }
        loading = viewModelScope.launch {
            when (val outcome = api.campaigns(participation)) {
                is Outcome.Success -> update { CampaignSelectionState.Loaded(participation, outcome.value) }
                is Outcome.Failure -> update { CampaignSelectionState.Failed(participation, outcome.error) }
            }
        }
    }
}
