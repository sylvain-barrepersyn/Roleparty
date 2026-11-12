package app.roleparty.campaign.tabletop.frontend.container.starting

import app.roleparty.core.frontend.container.ViewModelState

sealed interface GameSessionStartingState : ViewModelState {

    data class Ready(val started: Boolean = false, val starting: Boolean = false) : GameSessionStartingState
}
