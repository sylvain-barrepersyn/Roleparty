package app.roleparty.campaign.story.frontend.container.scene

import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.contract.web.SceneResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface ScenesState : ViewModelState {

    data object Loading : ScenesState

    data class Loaded(
        val scenes: List<SceneResponseBody>,
        val libraryEntries: List<LibraryEntryResponseBody>,
        val selectedId: SceneId? = null,
        val failure: BackendFailure? = null,
    ) : ScenesState {

        val selected get() = scenes.firstOrNull { it.id == selectedId }
    }

    data class Failed(val failure: BackendFailure) : ScenesState
}
