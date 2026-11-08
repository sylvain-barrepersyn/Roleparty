package app.roleparty.campaign.creature.frontend.container.monster

import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface MonsterSheetsAction : ViewModelAction {

    data object Retry : MonsterSheetsAction

    data object Create : MonsterSheetsAction

    data class Select(val monsterSheetId: MonsterSheetId) : MonsterSheetsAction

    data object StartEditing : MonsterSheetsAction

    data class Change(val sheet: MonsterSheetBody) : MonsterSheetsAction

    data object CancelEditing : MonsterSheetsAction

    data object Save : MonsterSheetsAction

    data object Duplicate : MonsterSheetsAction
}
