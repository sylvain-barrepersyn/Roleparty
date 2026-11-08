package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.domain.port.primary.JoinCampaign
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.domain.port.secondary.Invitations
import app.roleparty.core.commons.Outcome

internal class CampaignJoiningService(
    private val campaigns: Campaigns,
    private val invitations: Invitations,
) : JoinCampaign {

    override suspend fun invoke(player: AccountId, code: Invitation.Code): Outcome<CampaignId, CampaignError> {
        val invitation = invitations.findByCode(code) ?: return Outcome.Failure(CampaignError.InvalidInvitationCode)
        if (invitation.used) return Outcome.Failure(CampaignError.InvitationCodeAlreadyUsed)

        val campaign = campaigns.findById(invitation.campaignId) ?: return Outcome.Failure(CampaignError.InvalidInvitationCode)
        if (player in campaign) return Outcome.Failure(CampaignError.AlreadyParticipant)

        if (!campaigns.addPlayer(campaign.id, player, code)) return Outcome.Failure(CampaignError.InvitationCodeAlreadyUsed)
        return Outcome.Success(campaign.id)
    }
}
