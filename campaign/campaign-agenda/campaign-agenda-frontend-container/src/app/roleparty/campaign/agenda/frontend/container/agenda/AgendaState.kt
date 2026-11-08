package app.roleparty.campaign.agenda.frontend.container.agenda

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface AgendaState : ViewModelState {

    data object Loading : AgendaState

    data class Loaded(
        val gameSessions: List<GameSessionResponseBody>,
        val selectedId: GameSessionId? = null,
        val scheduling: Boolean = false,
        val failure: BackendFailure? = null,
    ) : AgendaState {

        val selected get() = gameSessions.firstOrNull { it.id == selectedId }
    }

    data class Failed(val failure: BackendFailure) : AgendaState
}
