package app.roleparty.campaign.creature.frontend.container.playercharacter.creation

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface PlayerCharacterCreationEvent : ViewModelEvent {

    data class PlayerCharacterCreated(val playerCharacterId: PlayerCharacterId) : PlayerCharacterCreationEvent

    data object CreationCancelled : PlayerCharacterCreationEvent
}
