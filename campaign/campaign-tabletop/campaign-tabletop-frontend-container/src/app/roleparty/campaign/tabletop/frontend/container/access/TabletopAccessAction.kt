package app.roleparty.campaign.tabletop.frontend.container.access

import app.roleparty.core.frontend.container.ViewModelAction

sealed interface TabletopAccessAction : ViewModelAction {

    data object Open : TabletopAccessAction

    data object Close : TabletopAccessAction
}
