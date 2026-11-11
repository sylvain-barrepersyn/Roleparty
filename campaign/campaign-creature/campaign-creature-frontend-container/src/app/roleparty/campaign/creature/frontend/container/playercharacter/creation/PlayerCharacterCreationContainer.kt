package app.roleparty.campaign.creature.frontend.container.playercharacter.creation

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.PlayerCharacterCreationForm
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.imagefield.ImageFieldContainer

class PlayerCharacterCreationContainer(
    campaignId: CampaignId,
    private val onPlayerCharacterCreated: (PlayerCharacterId) -> Unit,
    private val onCancelled: () -> Unit,
) : Container<PlayerCharacterCreationState, PlayerCharacterCreationAction, PlayerCharacterCreationEvent, PlayerCharacterCreationViewModel>(
    PlayerCharacterCreationViewModel::class,
    campaignId,
) {

    @Composable
    override fun Content(state: PlayerCharacterCreationState, handle: (PlayerCharacterCreationAction) -> Unit) {
        when (state) {
            is PlayerCharacterCreationState.Form -> PlayerCharacterCreationForm(
                draft = state.draft,
                step = state.step,
                submitting = state.submitting,
                error = state.failure?.message(),
                onDraftChange = { handle(PlayerCharacterCreationAction.ChangeDraft(it)) },
                onStepChange = { handle(PlayerCharacterCreationAction.ShowStep(it)) },
                onRollScores = { handle(PlayerCharacterCreationAction.RollAbilityScores) },
                onSubmit = { handle(PlayerCharacterCreationAction.Submit) },
                onCancel = { handle(PlayerCharacterCreationAction.Cancel) },
                imageField = { label, modifier ->
                    ImageFieldContainer(
                        fieldId = "player-character-creation",
                        label = label,
                        value = state.draft.imageId,
                        onValueChange = { handle(PlayerCharacterCreationAction.ChangeDraft(state.draft.copy(imageId = it))) },
                        modifier = modifier,
                        imageSize = DpSize(200.dp, 200.dp),
                        required = true,
                    ).Render()
                },
            )
        }
    }

    override fun onEvent(event: PlayerCharacterCreationEvent) = when (event) {
        is PlayerCharacterCreationEvent.PlayerCharacterCreated -> onPlayerCharacterCreated(event.playerCharacterId)
        PlayerCharacterCreationEvent.CreationCancelled -> onCancelled()
    }
}
