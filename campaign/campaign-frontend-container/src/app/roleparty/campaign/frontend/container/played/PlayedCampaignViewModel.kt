package app.roleparty.campaign.frontend.container.played

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class PlayedCampaignViewModel(private val api: CreatureApi, private val playerCharacterId: PlayerCharacterId) :
    ViewModel<PlayedCampaignState, PlayedCampaignAction, PlayedCampaignEvent>(PlayedCampaignState.Loading) {

    init {
        load()
    }

    override fun handle(action: PlayedCampaignAction) {
        when (action) {
            PlayedCampaignAction.Retry -> load()
            is PlayedCampaignAction.ShowSection -> update { (it as? PlayedCampaignState.Loaded)?.copy(section = action.section) ?: it }
            PlayedCampaignAction.Exit -> emit(PlayedCampaignEvent.Exited)
        }
    }

    private fun load() {
        update { PlayedCampaignState.Loading }
        viewModelScope.launch {
            when (val outcome = api.playerCharacter(playerCharacterId)) {
                is Outcome.Success -> update { PlayedCampaignState.Loaded(outcome.value) }
                is Outcome.Failure -> update { PlayedCampaignState.Failed(outcome.error) }
            }
        }
    }
}
