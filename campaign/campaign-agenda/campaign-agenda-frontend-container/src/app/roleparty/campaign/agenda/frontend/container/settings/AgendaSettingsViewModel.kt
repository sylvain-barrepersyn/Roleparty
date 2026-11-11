package app.roleparty.campaign.agenda.frontend.container.settings

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResponseBody
import app.roleparty.campaign.agenda.frontend.container.data.AgendaApi
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class AgendaSettingsViewModel(private val api: AgendaApi, private val campaignId: CampaignId) :
    ViewModel<AgendaSettingsState, AgendaSettingsAction, AgendaSettingsEvent>(AgendaSettingsState.Loading) {

    init {
        load()
    }

    override fun handle(action: AgendaSettingsAction) {
        when (action) {
            AgendaSettingsAction.Retry -> load()
            is AgendaSettingsAction.ChangePresence -> change { copy(presence = action.presence) }
            is AgendaSettingsAction.ChangeDigitalGamingTable -> change { copy(digitalGamingTable = action.available) }
        }
    }

    private fun load() {
        update { AgendaSettingsState.Loading }
        viewModelScope.launch {
            when (val outcome = api.preference(campaignId)) {
                is Outcome.Success -> update { AgendaSettingsState.Loaded(outcome.value) }
                is Outcome.Failure -> update { AgendaSettingsState.Failed(outcome.error) }
            }
        }
    }

    private fun change(transform: AgendaPreferenceResponseBody.() -> AgendaPreferenceResponseBody) {
        val loaded = state.value as? AgendaSettingsState.Loaded ?: return
        val previous = loaded.preference
        val changed = previous.transform()
        update { AgendaSettingsState.Loaded(changed) }
        viewModelScope.launch {
            when (val outcome = api.changePreference(campaignId, changed.presence, changed.digitalGamingTable)) {
                is Outcome.Success -> Unit
                is Outcome.Failure -> update { AgendaSettingsState.Loaded(previous, outcome.error) }
            }
        }
    }
}
