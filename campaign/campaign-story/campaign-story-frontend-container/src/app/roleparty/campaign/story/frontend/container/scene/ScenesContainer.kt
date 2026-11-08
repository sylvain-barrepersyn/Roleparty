package app.roleparty.campaign.story.frontend.container.scene

import androidx.compose.runtime.Composable
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.frontend.component.Scenes
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class ScenesContainer(campaignId: CampaignId) :
    Container<ScenesState, ScenesAction, ScenesEvent, ScenesViewModel>(ScenesViewModel::class, campaignId) {

    @Composable
    override fun Content(state: ScenesState, handle: (ScenesAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            ScenesState.Loading -> FullScreenLoader()
            is ScenesState.Failed -> RetryableFailure(state.failure) { handle(ScenesAction.Retry) }
            is ScenesState.Loaded -> Scenes(
                scenes = state.scenes,
                selected = state.selected,
                libraryEntries = state.libraryEntries,
                error = state.failure?.message(),
                imageUrl = media::imageUrl,
                onSelect = { handle(ScenesAction.Select(it)) },
                onCreate = { handle(ScenesAction.Create(it)) },
                onNameChange = { handle(ScenesAction.ChangeName(it)) },
                onDescriptionChange = { handle(ScenesAction.ChangeDescription(it)) },
                onDelete = { handle(ScenesAction.Delete) },
                onLinkLibraryEntry = { handle(ScenesAction.LinkLibraryEntry(it)) },
                onUnlinkLibraryEntry = { handle(ScenesAction.UnlinkLibraryEntry(it)) },
            )
        }
    }
}
