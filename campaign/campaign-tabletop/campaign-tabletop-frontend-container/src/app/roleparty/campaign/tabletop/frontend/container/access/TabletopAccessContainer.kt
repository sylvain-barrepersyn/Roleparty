package app.roleparty.campaign.tabletop.frontend.container.access

import androidx.compose.runtime.Composable
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.frontend.component.OpenTabletop
import app.roleparty.campaign.tabletop.frontend.component.gameSessionStartedMessage
import app.roleparty.campaign.tabletop.frontend.container.TabletopParticipation
import app.roleparty.campaign.tabletop.frontend.container.tabletop.TabletopContainer
import app.roleparty.core.designsystem.toast.Toast
import app.roleparty.core.frontend.container.Container
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class TabletopAccessContainer(private val campaignId: CampaignId, private val participation: TabletopParticipation) :
    Container<TabletopAccessState, TabletopAccessAction, TabletopAccessEvent, TabletopAccessViewModel>(TabletopAccessViewModel::class, campaignId) {

    @Composable
    override fun Content(state: TabletopAccessState, handle: (TabletopAccessAction) -> Unit) {
        val gameSession = (state as? TabletopAccessState.Loaded)?.gameSession ?: return
        val media = koinInject<MediaApi>()

        OpenTabletop(attendees = state.attendees, imageUrl = media::imageUrl, onClick = { handle(TabletopAccessAction.Open) })
        if (state.opened) {
            TabletopContainer(campaignId, participation, gameSession, onClosed = { handle(TabletopAccessAction.Close) }).Render()
        }
    }

    override fun onEvent(event: TabletopAccessEvent) = when (event) {
        is TabletopAccessEvent.GameSessionStarted -> Toast.show { gameSessionStartedMessage(event.gameSession) }
    }
}
