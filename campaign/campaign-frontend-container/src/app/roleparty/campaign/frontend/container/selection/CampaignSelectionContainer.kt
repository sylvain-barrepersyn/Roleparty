package app.roleparty.campaign.frontend.container.selection

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.component.selection.CampaignSelection
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class CampaignSelectionContainer(
    private val onMasteredCampaignOpened: (CampaignId) -> Unit,
    private val onCampaignCreationRequested: () -> Unit,
    private val onCampaignJoiningRequested: () -> Unit,
    private val footer: @Composable RowScope.() -> Unit = {},
) : Container<CampaignSelectionState, CampaignSelectionAction, CampaignSelectionEvent, CampaignSelectionViewModel>(CampaignSelectionViewModel::class) {

    @Composable
    override fun Content(state: CampaignSelectionState, handle: (CampaignSelectionAction) -> Unit) {
        val media = koinInject<MediaApi>()
        LaunchedEffect(Unit) { handle(CampaignSelectionAction.Refresh) }

        CampaignSelection(
            participation = state.participation,
            campaigns = (state as? CampaignSelectionState.Loaded)?.campaigns,
            error = (state as? CampaignSelectionState.Failed)?.failure?.message(),
            imageUrl = media::imageUrl,
            onParticipationChange = { handle(CampaignSelectionAction.ShowParticipation(it)) },
            onRefresh = { handle(CampaignSelectionAction.Refresh) },
            onSelect = if (state.participation == Participation.GAME_MASTER) { id -> handle(CampaignSelectionAction.OpenMasteredCampaign(id)) } else null,
            onCreate = { handle(CampaignSelectionAction.CreateCampaign) },
            onJoin = { handle(CampaignSelectionAction.JoinCampaign) },
            footer = footer,
        )
    }

    override fun onEvent(event: CampaignSelectionEvent) = when (event) {
        is CampaignSelectionEvent.MasteredCampaignOpened -> onMasteredCampaignOpened(event.campaignId)
        CampaignSelectionEvent.CampaignCreationRequested -> onCampaignCreationRequested()
        CampaignSelectionEvent.CampaignJoiningRequested -> onCampaignJoiningRequested()
    }
}
