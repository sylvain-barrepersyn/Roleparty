package app.roleparty.campaign.creature.frontend.container.creatures

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.component.CreatureSelection
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CreaturesViewModel(private val api: CreatureApi, private val campaignId: CampaignId) :
    ViewModel<CreaturesState, CreaturesAction, CreaturesEvent>(CreaturesState.Loading) {

    init {
        load(select = null, showLoading = true)
    }

    override fun handle(action: CreaturesAction) {
        when (action) {
            CreaturesAction.Retry -> load(select = null, showLoading = true)
            is CreaturesAction.Select -> loaded { copy(selected = action.selection, failure = null) }
            CreaturesAction.CreateMonster -> createMonster()
            is CreaturesAction.Refresh -> load(select = action.select, showLoading = false)
        }
    }

    private fun load(select: CreatureSelection?, showLoading: Boolean) {
        if (showLoading) update { CreaturesState.Loading }
        viewModelScope.launch {
            val monsters = async { api.monsterSheets(campaignId) }
            val characters = async { api.characterSheets(campaignId) }
            val loadedMonsters = monsters.await()
            val loadedCharacters = characters.await()
            update { current ->
                when {
                    loadedMonsters is Outcome.Failure -> CreaturesState.Failed(loadedMonsters.error)
                    loadedCharacters is Outcome.Failure -> CreaturesState.Failed(loadedCharacters.error)
                    else -> CreaturesState.Loaded(
                        monsters = (loadedMonsters as Outcome.Success).value,
                        characters = (loadedCharacters as Outcome.Success).value,
                        selected = select ?: (current as? CreaturesState.Loaded)?.selected,
                    )
                }
            }
        }
    }

    private fun createMonster() {
        if (state.value !is CreaturesState.Loaded) return
        viewModelScope.launch {
            when (val outcome = api.createMonsterSheet(campaignId)) {
                is Outcome.Success -> load(select = CreatureSelection.Monster(outcome.value), showLoading = false)
                is Outcome.Failure -> loaded { copy(failure = outcome.error) }
            }
        }
    }

    private fun loaded(transform: CreaturesState.Loaded.() -> CreaturesState.Loaded) = update { (it as? CreaturesState.Loaded)?.transform() ?: it }
}
