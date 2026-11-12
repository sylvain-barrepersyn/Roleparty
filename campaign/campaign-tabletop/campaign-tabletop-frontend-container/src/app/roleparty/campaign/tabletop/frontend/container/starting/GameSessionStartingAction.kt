package app.roleparty.campaign.tabletop.frontend.container.starting

import app.roleparty.core.frontend.container.ViewModelAction

sealed interface GameSessionStartingAction : ViewModelAction {

    data object Refresh : GameSessionStartingAction

    data object Start : GameSessionStartingAction
}
