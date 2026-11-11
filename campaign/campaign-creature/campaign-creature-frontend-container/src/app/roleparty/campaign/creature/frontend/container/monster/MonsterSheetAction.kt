package app.roleparty.campaign.creature.frontend.container.monster

import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface MonsterSheetAction : ViewModelAction {

    data object Retry : MonsterSheetAction

    data object StartEditing : MonsterSheetAction

    data class Change(val sheet: MonsterSheetBody) : MonsterSheetAction

    data object CancelEditing : MonsterSheetAction

    data object Save : MonsterSheetAction

    data object Duplicate : MonsterSheetAction

    data object CreateCharacter : MonsterSheetAction
}
