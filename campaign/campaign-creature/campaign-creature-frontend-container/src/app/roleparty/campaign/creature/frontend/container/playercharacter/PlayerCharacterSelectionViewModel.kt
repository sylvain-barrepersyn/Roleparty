package app.roleparty.campaign.creature.frontend.container.playercharacter

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class PlayerCharacterSelectionViewModel(private val api: CreatureApi, private val campaignId: CampaignId) :
    ViewModel<PlayerCharacterSelectionState, PlayerCharacterSelectionAction, PlayerCharacterSelectionEvent>(PlayerCharacterSelectionState.Loading) {

    private var creationProposed = false

    override fun handle(action: PlayerCharacterSelectionAction) {
        when (action) {
            PlayerCharacterSelectionAction.Refresh -> load()
            is PlayerCharacterSelectionAction.OpenPlayerCharacter -> emit(PlayerCharacterSelectionEvent.PlayerCharacterOpened(action.playerCharacterId))
            PlayerCharacterSelectionAction.CreatePlayerCharacter -> emit(PlayerCharacterSelectionEvent.PlayerCharacterCreationRequested)
            PlayerCharacterSelectionAction.Exit -> emit(PlayerCharacterSelectionEvent.Exited)
        }
    }

    private fun load() {
        update { PlayerCharacterSelectionState.Loading }
        viewModelScope.launch {
            when (val outcome = api.playerCharacters(campaignId)) {
                is Outcome.Success -> {
                    update { PlayerCharacterSelectionState.Loaded(outcome.value) }
                    if (outcome.value.isEmpty() && !creationProposed) {
                        creationProposed = true
                        emit(PlayerCharacterSelectionEvent.PlayerCharacterCreationRequested)
                    }
                }
                is Outcome.Failure -> update { PlayerCharacterSelectionState.Failed(outcome.error) }
            }
        }
    }
}
