package app.roleparty.campaign.library.frontend.container.journal

import androidx.compose.runtime.Composable
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.frontend.component.Journal
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.imagefield.ImageFieldContainer
import org.koin.compose.koinInject

class JournalContainer(playerCharacterId: PlayerCharacterId) :
    Container<JournalState, JournalAction, JournalEvent, JournalViewModel>(JournalViewModel::class, playerCharacterId) {

    @Composable
    override fun Content(state: JournalState, handle: (JournalAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            JournalState.Loading -> FullScreenLoader()
            is JournalState.Failed -> RetryableFailure(state.failure) { handle(JournalAction.Retry) }
            is JournalState.Loaded -> Journal(
                entries = state.entries,
                selected = state.selected,
                creating = state.creating,
                error = state.failure?.message(),
                imageUrl = media::imageUrl,
                onCreate = { handle(JournalAction.CreateEntry) },
                onSelect = { handle(JournalAction.Select(it)) },
                onTitleChange = { handle(JournalAction.ChangeTitle(it)) },
                onTextChange = { handle(JournalAction.ChangeText(it)) },
                onRemoveImage = { handle(JournalAction.RemoveImage(it)) },
                imageField = { label, modifier ->
                    ImageFieldContainer(
                        fieldId = "journal-entry-image",
                        label = label,
                        value = null,
                        onValueChange = { handle(JournalAction.AddImage(it)) },
                        modifier = modifier,
                    ).Render()
                },
            )
        }
    }
}
