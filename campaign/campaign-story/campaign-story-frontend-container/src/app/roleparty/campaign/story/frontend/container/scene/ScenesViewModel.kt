package app.roleparty.campaign.story.frontend.container.scene

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.contract.web.SceneResponseBody
import app.roleparty.campaign.story.frontend.container.data.StoryApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.document.Document
import app.roleparty.core.frontend.container.Debouncer
import app.roleparty.core.frontend.container.ViewModel
import app.roleparty.core.ktor.client.BackendFailure
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScenesViewModel(private val api: StoryApi, private val campaignId: CampaignId) :
    ViewModel<ScenesState, ScenesAction, ScenesEvent>(ScenesState.Loading) {

    private val debouncer = Debouncer(viewModelScope)

    init {
        load()
    }

    override fun handle(action: ScenesAction) {
        when (action) {
            ScenesAction.Retry -> load()
            is ScenesAction.Select -> loaded { copy(selectedId = action.sceneId, failure = null) }
            is ScenesAction.Create -> create(action.name)
            is ScenesAction.ChangeName -> editSelected { it.copy(name = action.name) }
            is ScenesAction.ChangeDescription -> editSelected { it.copy(description = action.description) }
            ScenesAction.Delete -> delete()
            is ScenesAction.LinkLibraryEntry -> changeLink(action.libraryEntryId, linked = true)
            is ScenesAction.UnlinkLibraryEntry -> changeLink(action.libraryEntryId, linked = false)
        }
    }

    private fun load() {
        update { ScenesState.Loading }
        viewModelScope.launch {
            val scenes = async { api.scenes(campaignId) }
            val libraryEntries = async { api.libraryEntries(campaignId) }
            val loadedScenes = scenes.await()
            val loadedLibraryEntries = libraryEntries.await()
            update {
                when {
                    loadedScenes is Outcome.Failure -> ScenesState.Failed(loadedScenes.error)
                    loadedLibraryEntries is Outcome.Failure -> ScenesState.Failed(loadedLibraryEntries.error)
                    else -> ScenesState.Loaded(
                        scenes = (loadedScenes as Outcome.Success).value,
                        libraryEntries = (loadedLibraryEntries as Outcome.Success).value.sortedBy { it.title.lowercase() },
                    )
                }
            }
        }
    }

    private fun create(name: String) {
        if (state.value !is ScenesState.Loaded) return
        viewModelScope.launch {
            when (val outcome = api.createScene(campaignId, name, Document.EMPTY)) {
                is Outcome.Success -> loaded {
                    copy(scenes = scenes + SceneResponseBody(outcome.value, name, Document.EMPTY, emptyList()), selectedId = outcome.value, failure = null)
                }
                is Outcome.Failure -> fail(outcome.error)
            }
        }
    }

    private fun editSelected(transform: (SceneResponseBody) -> SceneResponseBody) {
        val sceneId = (state.value as? ScenesState.Loaded)?.selectedId ?: return
        loaded { copy(scenes = scenes.map { if (it.id == sceneId) transform(it) else it }) }
        debouncer.submit(sceneId) { save(sceneId) }
    }

    private suspend fun save(sceneId: SceneId) {
        val scene = (state.value as? ScenesState.Loaded)?.scenes?.firstOrNull { it.id == sceneId } ?: return
        when (val outcome = api.changeScene(campaignId, sceneId, scene.name.trim(), scene.description)) {
            is Outcome.Success -> loaded { copy(failure = null) }
            is Outcome.Failure -> fail(outcome.error)
        }
    }

    private fun delete() {
        val sceneId = (state.value as? ScenesState.Loaded)?.selectedId ?: return
        viewModelScope.launch {
            when (val outcome = api.deleteScene(campaignId, sceneId)) {
                is Outcome.Success -> loaded { copy(scenes = scenes.filterNot { it.id == sceneId }, selectedId = null, failure = null) }
                is Outcome.Failure -> fail(outcome.error)
            }
        }
    }

    private fun changeLink(libraryEntryId: LibraryEntryId, linked: Boolean) {
        val sceneId = (state.value as? ScenesState.Loaded)?.selectedId ?: return
        viewModelScope.launch {
            val outcome =
                if (linked) api.linkLibraryEntry(campaignId, sceneId, libraryEntryId) else api.unlinkLibraryEntry(campaignId, sceneId, libraryEntryId)
            when (outcome) {
                is Outcome.Success -> loaded {
                    copy(
                        scenes = scenes.map { scene ->
                            if (scene.id != sceneId) scene
                            else scene.copy(libraryEntryIds = if (linked) scene.libraryEntryIds + libraryEntryId else scene.libraryEntryIds - libraryEntryId)
                        },
                        failure = null,
                    )
                }
                is Outcome.Failure -> fail(outcome.error)
            }
        }
    }

    private fun fail(failure: BackendFailure) = loaded { copy(failure = failure) }

    private fun loaded(transform: ScenesState.Loaded.() -> ScenesState.Loaded) = update { (it as? ScenesState.Loaded)?.transform() ?: it }
}
