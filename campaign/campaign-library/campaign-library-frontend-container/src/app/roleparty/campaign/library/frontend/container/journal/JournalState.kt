package app.roleparty.campaign.library.frontend.container.journal

import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.contract.web.JournalEntryResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface JournalState : ViewModelState {

    data object Loading : JournalState

    data class Loaded(
        val entries: List<JournalEntryResponseBody>,
        val selectedId: JournalEntryId? = null,
        val creating: Boolean = false,
        val failure: BackendFailure? = null,
    ) : JournalState {

        val selected get() = entries.firstOrNull { it.id == selectedId }
    }

    data class Failed(val failure: BackendFailure) : JournalState
}
