package app.roleparty.campaign.frontend.container.joining

import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface CampaignJoiningState : ViewModelState {

    data class Form(val code: String = "", val submitting: Boolean = false, val failure: BackendFailure? = null) : CampaignJoiningState
}
