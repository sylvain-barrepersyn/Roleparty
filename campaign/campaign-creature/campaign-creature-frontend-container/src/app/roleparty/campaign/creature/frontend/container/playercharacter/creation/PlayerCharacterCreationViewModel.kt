package app.roleparty.campaign.creature.frontend.container.playercharacter.creation

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.AbilityScoreMethod
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val ROLLED_SCORES = 6
private const val DICE_ROLLED = 4
private const val DICE_KEPT = 3
private const val DIE_FACES = 6

class PlayerCharacterCreationViewModel(
    private val api: CreatureApi,
    private val campaignId: CampaignId,
    private val random: Random = Random.Default,
) : ViewModel<PlayerCharacterCreationState, PlayerCharacterCreationAction, PlayerCharacterCreationEvent>(PlayerCharacterCreationState.Form()) {

    override fun handle(action: PlayerCharacterCreationAction) {
        when (action) {
            is PlayerCharacterCreationAction.ChangeDraft -> form { copy(draft = action.draft, failure = null) }
            is PlayerCharacterCreationAction.ShowStep -> form { if (draft.isReachable(action.step)) copy(step = action.step) else this }
            PlayerCharacterCreationAction.RollAbilityScores -> form {
                copy(draft = draft.copy(scoreMethod = AbilityScoreMethod.RANDOM, rolledScores = List(ROLLED_SCORES) { rollScore() }, baseScores = emptyMap()))
            }
            PlayerCharacterCreationAction.Submit -> submit()
            PlayerCharacterCreationAction.Cancel -> emit(PlayerCharacterCreationEvent.CreationCancelled)
        }
    }

    private fun rollScore(): Int = List(DICE_ROLLED) { random.nextInt(1, DIE_FACES + 1) }.sortedDescending().take(DICE_KEPT).sum()

    private fun submit() {
        val form = state.value as? PlayerCharacterCreationState.Form ?: return
        if (form.submitting) return
        val body = form.draft.toRequestBody() ?: return
        form { copy(submitting = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.createPlayerCharacter(campaignId, body)) {
                is Outcome.Success -> emit(PlayerCharacterCreationEvent.PlayerCharacterCreated(outcome.value))
                is Outcome.Failure -> form { copy(submitting = false, failure = outcome.error) }
            }
        }
    }

    private fun form(transform: PlayerCharacterCreationState.Form.() -> PlayerCharacterCreationState.Form) =
        update { (it as? PlayerCharacterCreationState.Form)?.transform() ?: it }
}
