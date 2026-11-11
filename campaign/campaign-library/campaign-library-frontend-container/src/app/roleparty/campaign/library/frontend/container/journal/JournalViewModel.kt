package app.roleparty.campaign.library.frontend.container.journal

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.contract.web.JournalEntryResponseBody
import app.roleparty.campaign.library.frontend.container.data.LibraryApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.Debouncer
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class JournalViewModel(private val api: LibraryApi, private val playerCharacterId: PlayerCharacterId) :
    ViewModel<JournalState, JournalAction, JournalEvent>(JournalState.Loading) {

    private val debouncer = Debouncer(viewModelScope)

    init {
        load()
    }

    override fun handle(action: JournalAction) {
        when (action) {
            JournalAction.Retry -> load()
            JournalAction.CreateEntry -> create()
            is JournalAction.Select -> loaded { copy(selectedId = action.entryId, failure = null) }
            is JournalAction.ChangeTitle -> editSelected(debounced = true) { it.copy(title = action.title) }
            is JournalAction.ChangeText -> editSelected(debounced = true) { it.copy(text = action.text) }
            is JournalAction.AddImage -> editSelected(debounced = false) { it.copy(images = (it.images + action.imageId).distinct()) }
            is JournalAction.RemoveImage -> editSelected(debounced = false) { it.copy(images = it.images - action.imageId) }
        }
    }

    private fun load() {
        update { JournalState.Loading }
        viewModelScope.launch {
            when (val outcome = api.journal(playerCharacterId)) {
                is Outcome.Success -> update { JournalState.Loaded(outcome.value, outcome.value.firstOrNull()?.id) }
                is Outcome.Failure -> update { JournalState.Failed(outcome.error) }
            }
        }
    }

    private fun create() {
        val loaded = state.value as? JournalState.Loaded ?: return
        if (loaded.creating) return

        loaded { copy(creating = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.createJournalEntry(playerCharacterId)) {
                is Outcome.Success -> loaded {
                    copy(entries = listOf(JournalEntryResponseBody(outcome.value, "", "", emptyList())) + entries, selectedId = outcome.value, creating = false)
                }
                is Outcome.Failure -> loaded { copy(creating = false, failure = outcome.error) }
            }
        }
    }

    private fun editSelected(debounced: Boolean, transform: (JournalEntryResponseBody) -> JournalEntryResponseBody) {
        val entryId = (state.value as? JournalState.Loaded)?.selectedId ?: return
        loaded { copy(entries = entries.map { if (it.id == entryId) transform(it) else it }) }
        if (debounced) debouncer.submit(entryId) { save(entryId) } else viewModelScope.launch { save(entryId) }
    }

    private suspend fun save(entryId: JournalEntryId) {
        val entry = (state.value as? JournalState.Loaded)?.entries?.firstOrNull { it.id == entryId } ?: return
        when (val outcome = api.updateJournalEntry(playerCharacterId, entry.copy(title = entry.title.trim()))) {
            is Outcome.Success -> loaded { copy(failure = null) }
            is Outcome.Failure -> loaded { copy(failure = outcome.error) }
        }
    }

    private fun loaded(transform: JournalState.Loaded.() -> JournalState.Loaded) = update { (it as? JournalState.Loaded)?.transform() ?: it }
}
