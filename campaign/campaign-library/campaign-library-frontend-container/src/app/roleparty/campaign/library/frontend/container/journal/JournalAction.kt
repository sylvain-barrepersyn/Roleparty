package app.roleparty.campaign.library.frontend.container.journal

import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.core.frontend.container.ViewModelAction
import app.roleparty.media.commons.ImageId

sealed interface JournalAction : ViewModelAction {

    data object Retry : JournalAction

    data object CreateEntry : JournalAction

    data class Select(val entryId: JournalEntryId) : JournalAction

    data class ChangeTitle(val title: String) : JournalAction

    data class ChangeText(val text: String) : JournalAction

    data class AddImage(val imageId: ImageId) : JournalAction

    data class RemoveImage(val imageId: ImageId) : JournalAction
}
