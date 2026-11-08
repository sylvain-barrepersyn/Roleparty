package app.roleparty.campaign.library.frontend.container.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.frontend.component.Library
import app.roleparty.campaign.library.frontend.component.LibraryEntryCreationDialog
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.imagefield.ImageFieldContainer
import org.koin.compose.koinInject

class LibraryContainer(campaignId: CampaignId) :
    Container<LibraryState, LibraryAction, LibraryEvent, LibraryViewModel>(LibraryViewModel::class, campaignId) {

    @Composable
    override fun Content(state: LibraryState, handle: (LibraryAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            LibraryState.Loading -> FullScreenLoader()
            is LibraryState.Failed -> RetryableFailure(state.failure) { handle(LibraryAction.Retry) }
            is LibraryState.Loaded -> {
                Library(
                    entries = state.entries,
                    selected = state.selected,
                    error = state.failure?.message(),
                    imageUrl = media::imageUrl,
                    onSelect = { handle(LibraryAction.Select(it)) },
                    onCreate = { handle(LibraryAction.OpenCreation) },
                    onTitleChange = { handle(LibraryAction.ChangeTitle(it)) },
                    onDescriptionChange = { handle(LibraryAction.ChangeDescription(it)) },
                )

                val creation = state.creation
                LibraryEntryCreationDialog(
                    displayed = creation != null,
                    title = creation?.title.orEmpty(),
                    description = creation?.description.orEmpty(),
                    submitting = creation?.submitting == true,
                    canSubmit = creation?.complete == true,
                    error = creation?.failure?.message(),
                    onTitleChange = { handle(LibraryAction.ChangeCreationTitle(it)) },
                    onDescriptionChange = { handle(LibraryAction.ChangeCreationDescription(it)) },
                    onSubmit = { handle(LibraryAction.SubmitCreation) },
                    onDismiss = { handle(LibraryAction.CancelCreation) },
                    imageField = { label, modifier ->
                        ImageFieldContainer(
                            fieldId = "library-entry-creation",
                            label = label,
                            value = creation?.imageId,
                            onValueChange = { handle(LibraryAction.ChangeCreationImage(it)) },
                            modifier = modifier,
                            imageSize = DpSize(320.dp, 200.dp),
                            required = true,
                        ).Render()
                    },
                )
            }
        }
    }
}
