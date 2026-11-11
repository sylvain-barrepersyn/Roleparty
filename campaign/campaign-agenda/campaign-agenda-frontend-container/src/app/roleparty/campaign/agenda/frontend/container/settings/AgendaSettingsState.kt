package app.roleparty.campaign.agenda.frontend.container.settings

import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface AgendaSettingsState : ViewModelState {

    data object Loading : AgendaSettingsState

    data class Loaded(val preference: AgendaPreferenceResponseBody, val failure: BackendFailure? = null) : AgendaSettingsState

    data class Failed(val failure: BackendFailure) : AgendaSettingsState
}
