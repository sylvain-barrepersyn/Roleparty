package app.roleparty.campaign.creature.frontend.container.playercharacter.creation

import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.PlayerCharacterCreationStep
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.PlayerCharacterDraft
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface PlayerCharacterCreationState : ViewModelState {

    data class Form(
        val draft: PlayerCharacterDraft = PlayerCharacterDraft(),
        val step: PlayerCharacterCreationStep = PlayerCharacterCreationStep.CLASS,
        val submitting: Boolean = false,
        val failure: BackendFailure? = null,
    ) : PlayerCharacterCreationState
}
