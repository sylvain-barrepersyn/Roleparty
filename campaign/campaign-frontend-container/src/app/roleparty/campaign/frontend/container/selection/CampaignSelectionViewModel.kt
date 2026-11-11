package app.roleparty.campaign.frontend.container.selection

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class CampaignSelectionViewModel(
    private val api: CampaignApi,
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) : ViewModel<CampaignSelectionState, CampaignSelectionAction, CampaignSelectionEvent>(CampaignSelectionState.Loading(Participation.GAME_MASTER)) {

    private var loading: Job? = null

    override fun handle(action: CampaignSelectionAction) {
        when (action) {
            is CampaignSelectionAction.ShowParticipation -> if (action.participation != state.value.participation) load(action.participation)
            CampaignSelectionAction.Refresh -> load(state.value.participation)
            is CampaignSelectionAction.OpenCampaign -> emit(
                when (state.value.participation) {
                    Participation.GAME_MASTER -> CampaignSelectionEvent.MasteredCampaignOpened(action.campaignId)
                    Participation.PLAYER -> CampaignSelectionEvent.PlayedCampaignOpened(action.campaignId)
                }
            )
            CampaignSelectionAction.CreateCampaign -> emit(CampaignSelectionEvent.CampaignCreationRequested)
            CampaignSelectionAction.JoinCampaign -> emit(CampaignSelectionEvent.CampaignJoiningRequested)
        }
    }

    private fun load(participation: Participation) {
        loading?.cancel()
        update { CampaignSelectionState.Loading(participation) }
        val today = clock.todayIn(timeZone)
        loading = viewModelScope.launch {
            when (val outcome = api.campaigns(participation, today)) {
                is Outcome.Success -> update { CampaignSelectionState.Loaded(participation, outcome.value, today) }
                is Outcome.Failure -> update { CampaignSelectionState.Failed(participation, outcome.error) }
            }
        }
    }
}
