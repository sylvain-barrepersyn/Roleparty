package app.roleparty.campaign.frontend.container.joining

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class CampaignJoiningViewModel(private val api: CampaignApi) :
    ViewModel<CampaignJoiningState, CampaignJoiningAction, CampaignJoiningEvent>(CampaignJoiningState.Form()) {

    override fun handle(action: CampaignJoiningAction) {
        when (action) {
            is CampaignJoiningAction.ChangeCode -> update { form(it).copy(code = action.code.uppercase()) }
            CampaignJoiningAction.Submit -> submit()
            CampaignJoiningAction.Cancel -> emit(CampaignJoiningEvent.JoiningCancelled)
        }
    }

    private fun submit() {
        val form = form(state.value)
        if (form.code.isBlank() || form.submitting) return

        update { form.copy(submitting = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.joinCampaign(form.code.trim())) {
                is Outcome.Success -> emit(CampaignJoiningEvent.CampaignJoined(outcome.value))
                is Outcome.Failure -> update { form(it).copy(submitting = false, failure = outcome.error) }
            }
        }
    }

    private fun form(state: CampaignJoiningState) = when (state) {
        is CampaignJoiningState.Form -> state
    }
}
