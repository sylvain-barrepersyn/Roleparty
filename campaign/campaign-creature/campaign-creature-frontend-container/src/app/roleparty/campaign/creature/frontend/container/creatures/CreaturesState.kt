package app.roleparty.campaign.creature.frontend.container.creatures

import app.roleparty.campaign.creature.contract.web.CharacterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.campaign.creature.frontend.component.CreatureSelection
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface CreaturesState : ViewModelState {

    data object Loading : CreaturesState

    data class Loaded(
        val monsters: List<MonsterSheetOverviewResponseBody>,
        val characters: List<CharacterSheetOverviewResponseBody>,
        val selected: CreatureSelection? = null,
        val failure: BackendFailure? = null,
    ) : CreaturesState

    data class Failed(val failure: BackendFailure) : CreaturesState
}
