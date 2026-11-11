package app.roleparty.campaign.creature.frontend.container.playercharacter.creation

import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.PlayerCharacterCreationStep
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.PlayerCharacterDraft
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface PlayerCharacterCreationAction : ViewModelAction {

    data class ChangeDraft(val draft: PlayerCharacterDraft) : PlayerCharacterCreationAction

    data class ShowStep(val step: PlayerCharacterCreationStep) : PlayerCharacterCreationAction

    data object RollAbilityScores : PlayerCharacterCreationAction

    data object Submit : PlayerCharacterCreationAction

    data object Cancel : PlayerCharacterCreationAction
}
