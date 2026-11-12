package app.roleparty.campaign.tabletop.frontend.container.tabletop

import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface TabletopEvent : ViewModelEvent {

    data object Closed : TabletopEvent
}
