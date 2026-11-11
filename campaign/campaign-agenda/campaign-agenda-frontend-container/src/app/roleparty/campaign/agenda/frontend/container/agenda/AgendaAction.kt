package app.roleparty.campaign.agenda.frontend.container.agenda

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.core.frontend.container.ViewModelAction
import kotlinx.datetime.LocalDate

sealed interface AgendaAction : ViewModelAction {

    data object Retry : AgendaAction

    data class Select(val gameSessionId: GameSessionId) : AgendaAction

    data object OpenScheduling : AgendaAction

    data object CancelScheduling : AgendaAction

    data class Schedule(val date: LocalDate) : AgendaAction

    data class ChangeName(val name: String) : AgendaAction

    data class ChangePresence(val participant: AccountId, val presence: PresenceName) : AgendaAction

    data class ChangeDigitalGamingTable(val available: Boolean) : AgendaAction
}
