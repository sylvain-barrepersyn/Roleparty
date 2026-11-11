package app.roleparty.campaign.story.frontend.container.scene

import app.roleparty.core.document.Document
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface ScenesAction : ViewModelAction {

    data object Retry : ScenesAction

    data class Select(val sceneId: SceneId) : ScenesAction

    data class Create(val name: String) : ScenesAction

    data class ChangeName(val name: String) : ScenesAction

    data class ChangeDescription(val description: Document) : ScenesAction

    data object Delete : ScenesAction

    data class LinkLibraryEntry(val libraryEntryId: LibraryEntryId) : ScenesAction

    data class UnlinkLibraryEntry(val libraryEntryId: LibraryEntryId) : ScenesAction
}
