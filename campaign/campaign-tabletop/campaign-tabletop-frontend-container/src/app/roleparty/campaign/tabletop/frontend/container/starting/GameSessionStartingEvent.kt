package app.roleparty.campaign.tabletop.frontend.container.starting

import app.roleparty.core.frontend.container.ViewModelEvent
import app.roleparty.core.ktor.client.BackendFailure

sealed interface GameSessionStartingEvent : ViewModelEvent {

    data class StartFailed(val failure: BackendFailure) : GameSessionStartingEvent
}
