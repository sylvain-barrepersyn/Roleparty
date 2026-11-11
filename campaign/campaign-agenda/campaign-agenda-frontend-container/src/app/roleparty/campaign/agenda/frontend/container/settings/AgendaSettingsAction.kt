package app.roleparty.campaign.agenda.frontend.container.settings

import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface AgendaSettingsAction : ViewModelAction {

    data object Retry : AgendaSettingsAction

    data class ChangePresence(val presence: PresenceName) : AgendaSettingsAction

    data class ChangeDigitalGamingTable(val available: Boolean) : AgendaSettingsAction
}
