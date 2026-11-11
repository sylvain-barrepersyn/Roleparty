package app.roleparty.campaign.creature.frontend.container.monster

import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface MonsterSheetState : ViewModelState {

    data object Loading : MonsterSheetState

    data class Loaded(
        val sheet: MonsterSheetBody,
        val saved: MonsterSheetBody,
        val editing: Boolean = false,
        val saving: Boolean = false,
        val failure: BackendFailure? = null,
    ) : MonsterSheetState {

        val dirty get() = sheet != saved
    }

    data class Failed(val failure: BackendFailure) : MonsterSheetState
}
