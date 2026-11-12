package app.roleparty.campaign.tabletop.frontend.container.starting

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class GameSessionStartingViewModel(
    private val api: TabletopApi,
    private val campaignId: CampaignId,
    private val gameSessionId: GameSessionId,
) : ViewModel<GameSessionStartingState, GameSessionStartingAction, GameSessionStartingEvent>(GameSessionStartingState.Ready()) {

    override fun handle(action: GameSessionStartingAction) {
        when (action) {
            GameSessionStartingAction.Refresh -> refresh()
            GameSessionStartingAction.Start -> start()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val tabletop = api.tabletop(campaignId)
            if (tabletop is Outcome.Success) ready { copy(started = tabletop.value.gameSession?.id == gameSessionId) }
        }
    }

    private fun start() {
        ready { copy(starting = true) }
        viewModelScope.launch {
            when (val outcome = api.startGameSession(campaignId, gameSessionId)) {
                is Outcome.Success -> ready { copy(started = true, starting = false) }
                is Outcome.Failure -> {
                    ready { copy(starting = false) }
                    emit(GameSessionStartingEvent.StartFailed(outcome.error))
                    refresh()
                }
            }
        }
    }

    private fun ready(transform: GameSessionStartingState.Ready.() -> GameSessionStartingState.Ready) =
        update { (it as GameSessionStartingState.Ready).transform() }
}
