package app.roleparty.campaign.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.core.commons.Outcome

interface InviteToCampaign {

    suspend fun generateCode(requester: AccountId, campaignId: CampaignId): Outcome<Invitation.Code, CampaignError>

    suspend fun invitations(requester: AccountId, campaignId: CampaignId): Outcome<List<Invitation>, CampaignError>
}
