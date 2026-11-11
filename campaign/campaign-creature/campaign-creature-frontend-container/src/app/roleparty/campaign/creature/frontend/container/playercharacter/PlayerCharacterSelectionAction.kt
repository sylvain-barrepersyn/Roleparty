package app.roleparty.campaign.creature.frontend.container.playercharacter

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface PlayerCharacterSelectionAction : ViewModelAction {

    data object Refresh : PlayerCharacterSelectionAction

    data class OpenPlayerCharacter(val playerCharacterId: PlayerCharacterId) : PlayerCharacterSelectionAction

    data object CreatePlayerCharacter : PlayerCharacterSelectionAction

    data object Exit : PlayerCharacterSelectionAction
}
