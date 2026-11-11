package app.roleparty.campaign.creature.frontend.container.playercharacter

import app.roleparty.campaign.creature.contract.web.PlayerCharacterOverviewResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface PlayerCharacterSelectionState : ViewModelState {

    data object Loading : PlayerCharacterSelectionState

    data class Loaded(val characters: List<PlayerCharacterOverviewResponseBody>) : PlayerCharacterSelectionState

    data class Failed(val failure: BackendFailure) : PlayerCharacterSelectionState
}
