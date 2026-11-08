package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.domain.port.primary.InviteToCampaign
import app.roleparty.campaign.domain.port.secondary.Invitations
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.getOrElse

internal class CampaignInvitingService(
    private val invitations: Invitations,
    private val access: CampaignAccessCheckingService,
) : InviteToCampaign {

    override suspend fun generateCode(requester: AccountId, campaignId: CampaignId): Outcome<Invitation.Code, CampaignError> {
        access.asGameMaster(requester, campaignId).getOrElse { return Outcome.Failure(it) }

        val invitation = Invitation(Invitation.Code.generate(), campaignId)
        invitations.create(invitation)
        return Outcome.Success(invitation.code)
    }

    override suspend fun invitations(requester: AccountId, campaignId: CampaignId): Outcome<List<Invitation>, CampaignError> {
        access.asGameMaster(requester, campaignId).getOrElse { return Outcome.Failure(it) }

        return Outcome.Success(invitations.ofCampaign(campaignId))
    }
}
