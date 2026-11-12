package app.roleparty.campaign.tabletop.frontend.container.tabletop

import app.roleparty.core.frontend.container.ViewModelAction

sealed interface TabletopAction : ViewModelAction {

    data object Join : TabletopAction

    data object Leave : TabletopAction

    data object Close : TabletopAction

    data object Retry : TabletopAction

    data class TurnCamera(val on: Boolean) : TabletopAction

    data class TurnMicrophone(val on: Boolean) : TabletopAction
}
