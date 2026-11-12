package app.roleparty.campaign.tabletop.frontend.container.starting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.frontend.component.StartGameSessionButton
import app.roleparty.core.designsystem.toast.Toast
import app.roleparty.core.designsystem.toast.ToastType
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.message

class GameSessionStartingContainer(campaignId: CampaignId, private val gameSessionId: GameSessionId) :
    Container<GameSessionStartingState, GameSessionStartingAction, GameSessionStartingEvent, GameSessionStartingViewModel>(
        GameSessionStartingViewModel::class,
        campaignId,
        gameSessionId,
    ) {

    @Composable
    override fun Content(state: GameSessionStartingState, handle: (GameSessionStartingAction) -> Unit) {
        LaunchedEffect(gameSessionId) { handle(GameSessionStartingAction.Refresh) }

        when (state) {
            is GameSessionStartingState.Ready -> StartGameSessionButton(
                started = state.started,
                starting = state.starting,
                onClick = { handle(GameSessionStartingAction.Start) },
            )
        }
    }

    override fun onEvent(event: GameSessionStartingEvent) = when (event) {
        is GameSessionStartingEvent.StartFailed -> Toast.show(ToastType.ERROR) { event.failure.message() }
    }
}
