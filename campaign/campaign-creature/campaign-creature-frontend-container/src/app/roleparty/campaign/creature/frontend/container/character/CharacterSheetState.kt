package app.roleparty.campaign.creature.frontend.container.character

import app.roleparty.campaign.creature.contract.web.CharacterSheetResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface CharacterSheetState : ViewModelState {

    data object Loading : CharacterSheetState

    data class Loaded(
        val character: CharacterSheetResponseBody,
        val saved: CharacterSheetResponseBody,
        val monster: MonsterSheetBody? = null,
        val editing: Boolean = false,
        val saving: Boolean = false,
        val failure: BackendFailure? = null,
    ) : CharacterSheetState {

        val dirty get() = character != saved
    }

    data class Failed(val failure: BackendFailure) : CharacterSheetState
}
