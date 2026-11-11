package app.roleparty.campaign.creature.frontend.container.character

import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface CharacterSheetEvent : ViewModelEvent {

    data object Saved : CharacterSheetEvent
}
