package app.roleparty.campaign.agenda.frontend.container.settings

import androidx.compose.runtime.Composable
import app.roleparty.campaign.agenda.frontend.component.AgendaSettings
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.designsystem.feedback.Loader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message

class AgendaSettingsContainer(campaignId: CampaignId) :
    Container<AgendaSettingsState, AgendaSettingsAction, AgendaSettingsEvent, AgendaSettingsViewModel>(AgendaSettingsViewModel::class, campaignId) {

    @Composable
    override fun Content(state: AgendaSettingsState, handle: (AgendaSettingsAction) -> Unit) {
        when (state) {
            AgendaSettingsState.Loading -> Loader()
            is AgendaSettingsState.Failed -> RetryableFailure(state.failure) { handle(AgendaSettingsAction.Retry) }
            is AgendaSettingsState.Loaded -> AgendaSettings(
                gameMaster = state.preference.gameMaster,
                presence = state.preference.presence,
                digitalGamingTable = state.preference.digitalGamingTable,
                error = state.failure?.message(),
                onPresenceChange = { handle(AgendaSettingsAction.ChangePresence(it)) },
                onDigitalGamingTableChange = { handle(AgendaSettingsAction.ChangeDigitalGamingTable(it)) },
            )
        }
    }
}
