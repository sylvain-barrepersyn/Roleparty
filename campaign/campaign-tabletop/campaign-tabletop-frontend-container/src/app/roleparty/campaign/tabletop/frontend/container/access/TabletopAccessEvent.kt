package app.roleparty.campaign.tabletop.frontend.container.access

import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface TabletopAccessEvent : ViewModelEvent {

    data class GameSessionStarted(val gameSession: StartedGameSessionBody) : TabletopAccessEvent
}
