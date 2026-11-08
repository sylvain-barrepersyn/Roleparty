package app.roleparty.campaign.library.frontend.container.library

import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.core.frontend.container.ViewModelAction
import app.roleparty.media.commons.ImageId

sealed interface LibraryAction : ViewModelAction {

    data object Retry : LibraryAction

    data class Select(val entryId: LibraryEntryId) : LibraryAction

    data class ChangeTitle(val title: String) : LibraryAction

    data class ChangeDescription(val description: String) : LibraryAction

    data object OpenCreation : LibraryAction

    data class ChangeCreationTitle(val title: String) : LibraryAction

    data class ChangeCreationDescription(val description: String) : LibraryAction

    data class ChangeCreationImage(val imageId: ImageId) : LibraryAction

    data object SubmitCreation : LibraryAction

    data object CancelCreation : LibraryAction
}
