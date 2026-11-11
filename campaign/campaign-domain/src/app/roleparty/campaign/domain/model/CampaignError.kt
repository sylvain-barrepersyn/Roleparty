package app.roleparty.campaign.domain.model

import app.roleparty.campaign.commons.CampaignId

sealed interface CampaignError {

    data class CampaignNotFound(val campaignId: CampaignId) : CampaignError

    data object GameMasterRequired : CampaignError

    data object ParticipantRequired : CampaignError

    data object BlankName : CampaignError

    data object InvalidInvitationCode : CampaignError

    data object InvitationCodeAlreadyUsed : CampaignError

    data object AlreadyParticipant : CampaignError
}
