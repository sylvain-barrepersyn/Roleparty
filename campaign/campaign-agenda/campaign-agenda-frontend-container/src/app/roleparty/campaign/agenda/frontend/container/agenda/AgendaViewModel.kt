package app.roleparty.campaign.agenda.frontend.container.agenda

import androidx.lifecycle.viewModelScope
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.contract.web.PresenceBody
import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.campaign.agenda.frontend.container.data.AgendaApi
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.Debouncer
import app.roleparty.core.frontend.container.ViewModel
import app.roleparty.core.ktor.client.BackendFailure
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
            is AgendaAction.ChangePresence -> changePresence(action.participant, action.presence)
            is AgendaAction.ChangeDigitalGamingTable -> changeDigitalGamingTable(action.available)
        }
    }

    private fun load(selectedId: GameSessionId? = null, failure: BackendFailure? = null) {
        if (state.value !is AgendaState.Loaded) update { AgendaState.Loading }
        viewModelScope.launch {
            when (val outcome = api.agenda(campaignId)) {
                is Outcome.Success -> update {
                    val agenda = outcome.value
                    AgendaState.Loaded(agenda.requester, agenda.participants, agenda.gameSessions.sortedBy { it.date }, selectedId, failure = failure)
                }
                is Outcome.Failure -> update { (it as? AgendaState.Loaded)?.copy(failure = failure ?: outcome.error) ?: AgendaState.Failed(outcome.error) }
            }
        }
    }

    private fun schedule(date: LocalDate) {
        loaded { copy(scheduling = false) }
        viewModelScope.launch {
            when (val outcome = api.scheduleGameSession(campaignId, date)) {
                is Outcome.Success -> load(selectedId = outcome.value)
                is Outcome.Failure -> loaded { copy(failure = outcome.error) }
            }
        }
    }

    private fun rename(name: String) {
        val gameSessionId = (state.value as? AgendaState.Loaded)?.selectedId ?: return
        editSelected { it.copy(name = name) }
        debouncer.submit(gameSessionId) { save(gameSessionId) }
    }

    private suspend fun save(gameSessionId: GameSessionId) {
        val name = (state.value as? AgendaState.Loaded)?.gameSessions?.firstOrNull { it.id == gameSessionId }?.name ?: return
        when (val outcome = api.renameGameSession(campaignId, gameSessionId, name.trim())) {
            is Outcome.Success -> loaded { copy(failure = null) }
            is Outcome.Failure -> loaded { copy(failure = outcome.error) }
        }
    }

    private fun changePresence(participant: AccountId, presence: PresenceName) {
        val gameSessionId = (state.value as? AgendaState.Loaded)?.selectedId ?: return
        editSelected { session -> session.copy(presences = session.presences.filterNot { it.accountId == participant } + PresenceBody(participant, presence)) }
        persist(gameSessionId) { api.changePresence(campaignId, gameSessionId, participant, presence) }
    }

    private fun changeDigitalGamingTable(available: Boolean) {
        val gameSessionId = (state.value as? AgendaState.Loaded)?.selectedId ?: return
        editSelected { it.copy(digitalGamingTable = available) }
        persist(gameSessionId) { api.changeDigitalGamingTable(campaignId, gameSessionId, available) }
    }

    private fun persist(gameSessionId: GameSessionId, call: suspend () -> Outcome<Unit, BackendFailure>) {
        viewModelScope.launch {
            when (val outcome = call()) {
                is Outcome.Success -> loaded { copy(failure = null) }
                is Outcome.Failure -> load(selectedId = gameSessionId, failure = outcome.error)
            }
        }
    }

    private fun editSelected(transform: (GameSessionResponseBody) -> GameSessionResponseBody) {
        val gameSessionId = (state.value as? AgendaState.Loaded)?.selectedId ?: return
        loaded { copy(gameSessions = gameSessions.map { if (it.id == gameSessionId) transform(it) else it }) }
    }

    private fun loaded(transform: AgendaState.Loaded.() -> AgendaState.Loaded) = update { (it as? AgendaState.Loaded)?.transform() ?: it }
}
