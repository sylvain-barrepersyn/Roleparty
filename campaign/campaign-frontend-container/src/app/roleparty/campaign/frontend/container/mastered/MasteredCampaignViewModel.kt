package app.roleparty.campaign.frontend.container.mastered

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class MasteredCampaignViewModel(private val api: CampaignApi, private val campaignId: CampaignId) :
    ViewModel<MasteredCampaignState, MasteredCampaignAction, MasteredCampaignEvent>(MasteredCampaignState.Loading) {

    init {
        load()
    }

    override fun handle(action: MasteredCampaignAction) {
        when (action) {
            MasteredCampaignAction.Retry -> load()
            is MasteredCampaignAction.ShowSection -> update { (it as? MasteredCampaignState.Loaded)?.copy(section = action.section) ?: it }
            MasteredCampaignAction.Exit -> emit(MasteredCampaignEvent.Exited)
        }
    }

    private fun load() {
        update { MasteredCampaignState.Loading }
        viewModelScope.launch {
            when (val outcome = api.campaign(campaignId)) {
                is Outcome.Success -> update { MasteredCampaignState.Loaded(outcome.value) }
                is Outcome.Failure -> update { MasteredCampaignState.Failed(outcome.error) }
            }
        }
    }
}
