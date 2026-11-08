package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.commons.CampaignId

internal class CampaignAccessCheckingService(private val campaigns: Campaigns) {

    suspend fun asGameMaster(requester: AccountId, campaignId: CampaignId): AgendaError? {
        val participants = campaigns.participantsOf(campaignId) ?: return AgendaError.CampaignNotFound(campaignId)
        return if (participants.gameMaster == requester) null else AgendaError.GameMasterRequired
    }

    suspend fun asParticipant(requester: AccountId, campaignId: CampaignId): AgendaError? {
        val participants = campaigns.participantsOf(campaignId) ?: return AgendaError.CampaignNotFound(campaignId)
        return if (requester in participants) null else AgendaError.ParticipantRequired
    }
}
