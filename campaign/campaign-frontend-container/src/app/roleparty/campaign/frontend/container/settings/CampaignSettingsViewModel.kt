package app.roleparty.campaign.frontend.container.settings

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class CampaignSettingsViewModel(private val api: CampaignApi, private val campaignId: CampaignId) :
    ViewModel<CampaignSettingsState, CampaignSettingsAction, CampaignSettingsEvent>(CampaignSettingsState.Loading) {

    init {
        load()
    }

    override fun handle(action: CampaignSettingsAction) {
        when (action) {
            CampaignSettingsAction.Retry -> load()
            CampaignSettingsAction.GenerateInvitation -> generate()
        }
    }

    private fun load() {
        update { CampaignSettingsState.Loading }
        viewModelScope.launch {
            when (val outcome = api.invitations(campaignId)) {
                is Outcome.Success -> update { CampaignSettingsState.Loaded(outcome.value) }
                is Outcome.Failure -> update { CampaignSettingsState.Failed(outcome.error) }
            }
        }
    }

    private fun generate() {
        val loaded = state.value as? CampaignSettingsState.Loaded ?: return
        if (loaded.generating) return

        update { loaded.copy(generating = true, failure = null) }
        viewModelScope.launch {
            val outcome = api.generateInvitation(campaignId)
            update {
                val current = it as? CampaignSettingsState.Loaded ?: return@update it
                when (outcome) {
                    is Outcome.Success -> current.copy(invitations = current.invitations + outcome.value, generating = false)
                    is Outcome.Failure -> current.copy(generating = false, failure = outcome.error)
                }
            }
        }
    }
}
