package app.roleparty.campaign.library.frontend.container.library

import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId

sealed interface LibraryState : ViewModelState {

    data object Loading : LibraryState

    data class Loaded(
        val entries: List<LibraryEntryResponseBody>,
        val selectedId: LibraryEntryId? = null,
        val creation: Creation? = null,
        val failure: BackendFailure? = null,
    ) : LibraryState {

        val selected get() = entries.firstOrNull { it.id == selectedId }
    }

    data class Failed(val failure: BackendFailure) : LibraryState

    data class Creation(
        val title: String = "",
        val description: String = "",
        val imageId: ImageId? = null,
        val submitting: Boolean = false,
        val failure: BackendFailure? = null,
    ) {
        val complete get() = title.isNotBlank() && imageId != null
    }
}
