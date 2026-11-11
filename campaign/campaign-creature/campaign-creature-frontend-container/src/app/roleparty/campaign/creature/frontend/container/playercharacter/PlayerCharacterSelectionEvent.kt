package app.roleparty.campaign.creature.frontend.container.playercharacter

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface PlayerCharacterSelectionEvent : ViewModelEvent {

    data class PlayerCharacterOpened(val playerCharacterId: PlayerCharacterId) : PlayerCharacterSelectionEvent

    data object PlayerCharacterCreationRequested : PlayerCharacterSelectionEvent

    data object Exited : PlayerCharacterSelectionEvent
}
