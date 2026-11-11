package app.roleparty.campaign.agenda.frontend.container.agenda

import androidx.compose.runtime.Composable
import app.roleparty.campaign.agenda.frontend.component.Agenda
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message

class AgendaContainer(campaignId: CampaignId, private val editable: Boolean = true) :
    Container<AgendaState, AgendaAction, AgendaEvent, AgendaViewModel>(AgendaViewModel::class, campaignId) {

    @Composable
    override fun Content(state: AgendaState, handle: (AgendaAction) -> Unit) {
        when (state) {
            AgendaState.Loading -> FullScreenLoader()
            is AgendaState.Failed -> RetryableFailure(state.failure) { handle(AgendaAction.Retry) }
            is AgendaState.Loaded -> Agenda(
                sessions = state.gameSessions,
                selected = state.selected,
                scheduling = state.scheduling,
                error = state.failure?.message(),
                onSelect = { handle(AgendaAction.Select(it)) },
                onCancelScheduling = { handle(AgendaAction.CancelScheduling) },
                onSchedule = { handle(AgendaAction.Schedule(it)) },
                participants = state.participants,
                requester = state.requester,
                onPresenceChange = { participant, presence -> handle(AgendaAction.ChangePresence(participant, presence)) },
                onDigitalGamingTableChange = { handle(AgendaAction.ChangeDigitalGamingTable(it)) },
                onOpenScheduling = if (editable) { { handle(AgendaAction.OpenScheduling) } } else null,
                onNameChange = if (editable) { { handle(AgendaAction.ChangeName(it)) } } else null,
            )
        }
    }
}
