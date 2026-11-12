package app.roleparty.campaign.frontend.container.played

import androidx.compose.runtime.Composable
import app.roleparty.campaign.agenda.frontend.container.agenda.AgendaContainer
import app.roleparty.campaign.agenda.frontend.container.settings.AgendaSettingsContainer
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.frontend.component.played.PlayedCampaignLayout
import app.roleparty.campaign.library.frontend.container.journal.JournalContainer
import app.roleparty.campaign.frontend.component.played.PlayedCampaignSection
import app.roleparty.campaign.creature.frontend.component.playercharacter.PlayerCharacterSheet
import app.roleparty.campaign.tabletop.frontend.container.TabletopParticipation
import app.roleparty.campaign.tabletop.frontend.container.access.TabletopAccessContainer
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.designsystem.layout.DetailPane
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.Navigation
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class PlayedCampaignContainer(
    private val campaignId: CampaignId,
    private val playerCharacterId: PlayerCharacterId,
    private val onExited: () -> Unit,
) : Container<PlayedCampaignState, PlayedCampaignAction, PlayedCampaignEvent, PlayedCampaignViewModel>(PlayedCampaignViewModel::class, playerCharacterId) {

    @Composable
    override fun Content(state: PlayedCampaignState, handle: (PlayedCampaignAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            PlayedCampaignState.Loading -> FullScreenLoader()
            is PlayedCampaignState.Failed -> RetryableFailure(state.failure) { handle(PlayedCampaignAction.Retry) }
            is PlayedCampaignState.Loaded -> PlayedCampaignLayout(
                characterName = state.character.name,
                characterLevel = state.character.level,
                avatarUrl = media.imageUrl(state.character.imageId),
                section = state.section,
                onSectionSelect = { handle(PlayedCampaignAction.ShowSection(it)) },
                onExit = { handle(PlayedCampaignAction.Exit) },
                actions = { TabletopAccessContainer(campaignId, TabletopParticipation.Player(playerCharacterId)).Render() },
            ) {
                Navigation(backStack = listOf(state.section), onBack = { handle(PlayedCampaignAction.Exit) }) { section ->
                    when (section) {
                        PlayedCampaignSection.AGENDA -> AgendaContainer(campaignId, editable = false).Render()
                        PlayedCampaignSection.SHEET -> PlayerCharacterSheet(state.character)
                        PlayedCampaignSection.JOURNAL -> JournalContainer(playerCharacterId).Render()
                        PlayedCampaignSection.SETTINGS -> DetailPane { AgendaSettingsContainer(campaignId).Render() }
                    }
                }
            }
        }
    }

    override fun onEvent(event: PlayedCampaignEvent) = when (event) {
        PlayedCampaignEvent.Exited -> onExited()
    }
}
