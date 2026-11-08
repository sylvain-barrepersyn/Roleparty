package app.roleparty.campaign.agenda.frontend.container.agenda

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.frontend.container.data.AgendaApi
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.Debouncer
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AgendaViewModel(private val api: AgendaApi, private val campaignId: CampaignId) :
    ViewModel<AgendaState, AgendaAction, AgendaEvent>(AgendaState.Loading) {

    private val debouncer = Debouncer(viewModelScope)

    init {
        load()
    }

    override fun handle(action: AgendaAction) {
        when (action) {
            AgendaAction.Retry -> load()
            is AgendaAction.Select -> loaded { copy(selectedId = action.gameSessionId, failure = null) }
            AgendaAction.OpenScheduling -> loaded { copy(scheduling = true) }
            AgendaAction.CancelScheduling -> loaded { copy(scheduling = false) }
            is AgendaAction.Schedule -> schedule(action.date)
            is AgendaAction.ChangeName -> rename(action.name)
        }
    }

    private fun load() {
        update { AgendaState.Loading }
        viewModelScope.launch {
            when (val outcome = api.gameSessions(campaignId)) {
                is Outcome.Success -> update { AgendaState.Loaded(outcome.value.sortedBy { it.date }) }
                is Outcome.Failure -> update { AgendaState.Failed(outcome.error) }
            }
        }
    }

    private fun schedule(date: LocalDate) {
        loaded { copy(scheduling = false) }
        viewModelScope.launch {
            when (val outcome = api.scheduleGameSession(campaignId, date)) {
                is Outcome.Success -> loaded {
                    copy(
                        gameSessions = (gameSessions + GameSessionResponseBody(outcome.value, date, null)).sortedBy { it.date },
                        selectedId = outcome.value,
                        failure = null,
                    )
                }
                is Outcome.Failure -> loaded { copy(failure = outcome.error) }
            }
        }
    }

    private fun rename(name: String) {
        val gameSessionId = (state.value as? AgendaState.Loaded)?.selectedId ?: return
        loaded { copy(gameSessions = gameSessions.map { if (it.id == gameSessionId) it.copy(name = name) else it }) }
        debouncer.submit(gameSessionId) { save(gameSessionId) }
    }

    private suspend fun save(gameSessionId: GameSessionId) {
        val name = (state.value as? AgendaState.Loaded)?.gameSessions?.firstOrNull { it.id == gameSessionId }?.name ?: return
        when (val outcome = api.renameGameSession(campaignId, gameSessionId, name.trim())) {
            is Outcome.Success -> loaded { copy(failure = null) }
            is Outcome.Failure -> loaded { copy(failure = outcome.error) }
        }
    }

    private fun loaded(transform: AgendaState.Loaded.() -> AgendaState.Loaded) = update { (it as? AgendaState.Loaded)?.transform() ?: it }
}
