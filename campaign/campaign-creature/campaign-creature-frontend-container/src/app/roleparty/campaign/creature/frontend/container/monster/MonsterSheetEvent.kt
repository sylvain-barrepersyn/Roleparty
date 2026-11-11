package app.roleparty.campaign.creature.frontend.container.monster

import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface MonsterSheetEvent : ViewModelEvent {

    data object Saved : MonsterSheetEvent

    data class Duplicated(val monsterSheetId: MonsterSheetId) : MonsterSheetEvent

    data class CharacterCreated(val characterSheetId: CharacterSheetId) : MonsterSheetEvent
}
