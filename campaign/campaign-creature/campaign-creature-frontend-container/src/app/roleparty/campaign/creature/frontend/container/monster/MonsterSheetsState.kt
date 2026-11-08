package app.roleparty.campaign.creature.frontend.container.monster

import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface MonsterSheetsState : ViewModelState {

    data object Loading : MonsterSheetsState

    data class Loaded(
        val overviews: List<MonsterSheetOverviewResponseBody>,
        val editor: Editor? = null,
        val failure: BackendFailure? = null,
    ) : MonsterSheetsState

    data class Failed(val failure: BackendFailure) : MonsterSheetsState

    data class Editor(
        val monsterSheetId: MonsterSheetId,
        val sheet: MonsterSheetBody? = null,
        val saved: MonsterSheetBody? = null,
        val editing: Boolean = false,
        val saving: Boolean = false,
        val failure: BackendFailure? = null,
    ) {
        val dirty get() = sheet != saved
    }
}
