package app.roleparty.campaign.frontend.container.creation

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class CampaignCreationViewModel(private val api: CampaignApi) :
    ViewModel<CampaignCreationState, CampaignCreationAction, CampaignCreationEvent>(CampaignCreationState.Form()) {

    override fun handle(action: CampaignCreationAction) {
        when (action) {
            is CampaignCreationAction.ChangeName -> edit { copy(name = action.name) }
            is CampaignCreationAction.ChangeDescription -> edit { copy(description = action.description) }
            is CampaignCreationAction.ChangeImage -> edit { copy(imageId = action.imageId) }
            CampaignCreationAction.Submit -> submit()
            CampaignCreationAction.Cancel -> emit(CampaignCreationEvent.CreationCancelled)
        }
    }

    private fun edit(transform: CampaignCreationState.Form.() -> CampaignCreationState.Form) = update { form(it).transform() }

    private fun submit() {
        val form = form(state.value)
        val imageId = form.imageId
        if (!form.complete || form.submitting || imageId == null) return

        update { form.copy(submitting = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.createCampaign(form.name.trim(), form.description.trim(), imageId)) {
                is Outcome.Success -> emit(CampaignCreationEvent.CampaignCreated(outcome.value))
                is Outcome.Failure -> update { form(it).copy(submitting = false, failure = outcome.error) }
            }
        }
    }

    private fun form(state: CampaignCreationState) = when (state) {
        is CampaignCreationState.Form -> state
    }
}
