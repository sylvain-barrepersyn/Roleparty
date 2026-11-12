package app.roleparty.campaign.tabletop.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.CampaignParticipants
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.domain.port.secondary.Campaigns
import app.roleparty.core.commons.Outcome

internal class CampaignAccessCheckingService(private val campaigns: Campaigns) {

    suspend fun asGameMaster(requester: AccountId, campaignId: CampaignId): TabletopError? {
        val participants = campaigns.participantsOf(campaignId) ?: return TabletopError.CampaignNotFound(campaignId)
        return if (participants.gameMaster == requester) null else TabletopError.GameMasterRequired
    }

    suspend fun asParticipant(requester: AccountId, campaignId: CampaignId): Outcome<CampaignParticipants, TabletopError> {
        val participants = campaigns.participantsOf(campaignId) ?: return Outcome.Failure(TabletopError.CampaignNotFound(campaignId))
        return if (requester in participants) Outcome.Success(participants) else Outcome.Failure(TabletopError.ParticipantRequired)
    }
}
