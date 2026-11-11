package app.roleparty.campaign.frontend.container.mastered

import androidx.compose.runtime.Composable
import app.roleparty.campaign.agenda.frontend.container.agenda.AgendaContainer
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.container.creatures.CreaturesContainer
import app.roleparty.campaign.frontend.component.mastered.MasteredCampaignLayout
import app.roleparty.campaign.frontend.component.mastered.MasteredCampaignSection
import app.roleparty.campaign.frontend.container.settings.CampaignSettingsContainer
import app.roleparty.campaign.library.frontend.container.library.LibraryContainer
import app.roleparty.campaign.story.frontend.container.scene.ScenesContainer
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.Navigation
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class MasteredCampaignContainer(
    private val campaignId: CampaignId,
    private val onExited: () -> Unit,
) : Container<MasteredCampaignState, MasteredCampaignAction, MasteredCampaignEvent, MasteredCampaignViewModel>(MasteredCampaignViewModel::class, campaignId) {

    @Composable
    override fun Content(state: MasteredCampaignState, handle: (MasteredCampaignAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            MasteredCampaignState.Loading -> FullScreenLoader()
            is MasteredCampaignState.Failed -> RetryableFailure(state.failure) { handle(MasteredCampaignAction.Retry) }
            is MasteredCampaignState.Loaded -> MasteredCampaignLayout(
                name = state.campaign.name,
                players = state.campaign.players.size,
                imageUrl = media.imageUrl(state.campaign.imageId),
                section = state.section,
                onSectionSelect = { handle(MasteredCampaignAction.ShowSection(it)) },
                onExit = { handle(MasteredCampaignAction.Exit) },
            ) {
                Navigation(backStack = listOf(state.section), onBack = { handle(MasteredCampaignAction.Exit) }) { section ->
                    when (section) {
                        MasteredCampaignSection.AGENDA -> AgendaContainer(campaignId).Render()
                        MasteredCampaignSection.SCENES -> ScenesContainer(campaignId).Render()
                        MasteredCampaignSection.LIBRARY -> LibraryContainer(campaignId).Render()
                        MasteredCampaignSection.CREATURES -> CreaturesContainer(campaignId).Render()
                        MasteredCampaignSection.SETTINGS -> CampaignSettingsContainer(campaignId, state.campaign).Render()
                    }
                }
            }
        }
    }

    override fun onEvent(event: MasteredCampaignEvent) = when (event) {
        MasteredCampaignEvent.Exited -> onExited()
    }
}
