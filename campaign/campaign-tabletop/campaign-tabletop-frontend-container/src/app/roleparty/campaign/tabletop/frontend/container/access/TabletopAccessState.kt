package app.roleparty.campaign.tabletop.frontend.container.access

import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.core.frontend.container.ViewModelState

sealed interface TabletopAccessState : ViewModelState {

    data object Loading : TabletopAccessState

    data class Loaded(
        val gameSession: StartedGameSessionBody?,
        val attendees: List<AttendeeBody> = emptyList(),
        val opened: Boolean = false,
    ) : TabletopAccessState
}
