package app.roleparty.campaign.creature.frontend.container.creatures

import app.roleparty.campaign.creature.frontend.component.CreatureSelection
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface CreaturesAction : ViewModelAction {

    data object Retry : CreaturesAction

    data class Select(val selection: CreatureSelection) : CreaturesAction

    data object CreateMonster : CreaturesAction

    data class Refresh(val select: CreatureSelection? = null) : CreaturesAction
}
