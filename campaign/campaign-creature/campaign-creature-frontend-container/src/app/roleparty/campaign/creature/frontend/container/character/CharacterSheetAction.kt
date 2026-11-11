package app.roleparty.campaign.creature.frontend.container.character

import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface CharacterSheetAction : ViewModelAction {

    data object Retry : CharacterSheetAction

    data object StartEditing : CharacterSheetAction

    data class Change(val sheet: MonsterSheetBody) : CharacterSheetAction

    data class ChangeNotes(val notes: String) : CharacterSheetAction

    data object CancelEditing : CharacterSheetAction

    data object Save : CharacterSheetAction

    data object Unlink : CharacterSheetAction
}
