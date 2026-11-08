package app.roleparty.campaign.library.frontend.container.library

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.library.frontend.container.data.LibraryApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.Debouncer
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class LibraryViewModel(private val api: LibraryApi, private val campaignId: CampaignId) :
    ViewModel<LibraryState, LibraryAction, LibraryEvent>(LibraryState.Loading) {

    private val debouncer = Debouncer(viewModelScope)

    init {
        load(selectedId = null)
    }

    override fun handle(action: LibraryAction) {
        when (action) {
            LibraryAction.Retry -> load(selectedId = null)
            is LibraryAction.Select -> loaded { copy(selectedId = action.entryId, failure = null) }
            is LibraryAction.ChangeTitle -> editSelected { it.copy(title = action.title) }
            is LibraryAction.ChangeDescription -> editSelected { it.copy(description = action.description) }
            LibraryAction.OpenCreation -> loaded { copy(creation = LibraryState.Creation()) }
            is LibraryAction.ChangeCreationTitle -> creation { copy(title = action.title) }
            is LibraryAction.ChangeCreationDescription -> creation { copy(description = action.description) }
            is LibraryAction.ChangeCreationImage -> creation { copy(imageId = action.imageId) }
            LibraryAction.SubmitCreation -> create()
            LibraryAction.CancelCreation -> loaded { copy(creation = null) }
        }
    }

    private fun load(selectedId: LibraryEntryId?) {
        update { LibraryState.Loading }
        viewModelScope.launch {
            when (val outcome = api.entries(campaignId)) {
                is Outcome.Success -> update { LibraryState.Loaded(outcome.value.sortedBy { it.title.lowercase() }, selectedId) }
                is Outcome.Failure -> update { LibraryState.Failed(outcome.error) }
            }
        }
    }

    private fun editSelected(transform: (LibraryEntryResponseBody) -> LibraryEntryResponseBody) {
        val entryId = (state.value as? LibraryState.Loaded)?.selectedId ?: return
        loaded { copy(entries = entries.map { if (it.id == entryId) transform(it) else it }) }
        debouncer.submit(entryId) { save(entryId) }
    }

    private suspend fun save(entryId: LibraryEntryId) {
        val entry = (state.value as? LibraryState.Loaded)?.entries?.firstOrNull { it.id == entryId } ?: return
        when (val outcome = api.changeEntryMetadata(campaignId, entryId, entry.title.trim(), entry.description.trim())) {
            is Outcome.Success -> loaded { copy(failure = null) }
            is Outcome.Failure -> loaded { copy(failure = outcome.error) }
        }
    }

    private fun create() {
        val creation = (state.value as? LibraryState.Loaded)?.creation ?: return
        val imageId = creation.imageId
        if (!creation.complete || creation.submitting || imageId == null) return

        creation { copy(submitting = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.createEntry(campaignId, creation.title.trim(), creation.description.trim(), imageId)) {
                is Outcome.Success -> load(selectedId = outcome.value)
                is Outcome.Failure -> creation { copy(submitting = false, failure = outcome.error) }
            }
        }
    }

    private fun loaded(transform: LibraryState.Loaded.() -> LibraryState.Loaded) = update { (it as? LibraryState.Loaded)?.transform() ?: it }

    private fun creation(transform: LibraryState.Creation.() -> LibraryState.Creation) = loaded { copy(creation = creation?.transform()) }
}
