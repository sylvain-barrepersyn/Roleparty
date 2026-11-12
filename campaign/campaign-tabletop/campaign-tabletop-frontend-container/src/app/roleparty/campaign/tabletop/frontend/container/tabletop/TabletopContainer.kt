package app.roleparty.campaign.tabletop.frontend.container.tabletop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.campaign.tabletop.frontend.component.Tabletop
import app.roleparty.campaign.tabletop.frontend.component.TabletopJoining
import app.roleparty.campaign.tabletop.frontend.component.TabletopWindow
import app.roleparty.campaign.tabletop.frontend.container.TabletopParticipation
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class TabletopContainer(
    campaignId: CampaignId,
    participation: TabletopParticipation,
    private val gameSession: StartedGameSessionBody,
    private val onClosed: () -> Unit,
) : Container<TabletopState, TabletopAction, TabletopEvent, TabletopViewModel>(TabletopViewModel::class, campaignId, participation) {

    @Composable
    override fun Content(state: TabletopState, handle: (TabletopAction) -> Unit) {
        val media = koinInject<MediaApi>()

        DisposableEffect(Unit) {
            handle(TabletopAction.Join)
            onDispose { handle(TabletopAction.Leave) }
        }

        TabletopWindow(gameSession = gameSession, onCloseRequest = { handle(TabletopAction.Close) }) {
            when (state) {
                TabletopState.Joining -> TabletopJoining()
                is TabletopState.Failed -> RetryableFailure(state.failure) { handle(TabletopAction.Retry) }
                is TabletopState.Joined -> Tabletop(
                    attendees = state.attendees,
                    camera = state.camera,
                    microphone = state.microphone,
                    cameraUnavailable = state.cameraUnavailable,
                    microphoneUnavailable = state.microphoneUnavailable,
                    imageUrl = media::imageUrl,
                    video = { accountId -> state.video(accountId).collectAsState().value },
                    onCameraChange = { handle(TabletopAction.TurnCamera(it)) },
                    onMicrophoneChange = { handle(TabletopAction.TurnMicrophone(it)) },
                )
            }
        }
    }

    override fun onEvent(event: TabletopEvent) = when (event) {
        TabletopEvent.Closed -> onClosed()
    }
}
