package app.roleparty.campaign.frontend.container.joining

import app.roleparty.core.frontend.container.ViewModelAction

sealed interface CampaignJoiningAction : ViewModelAction {

    data class ChangeCode(val code: String) : CampaignJoiningAction

    data object Submit : CampaignJoiningAction

    data object Cancel : CampaignJoiningAction
}
