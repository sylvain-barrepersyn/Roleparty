package app.roleparty.campaign.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.core.commons.Outcome

interface JoinCampaign {

    suspend operator fun invoke(player: AccountId, code: Invitation.Code): Outcome<CampaignId, CampaignError>
}
