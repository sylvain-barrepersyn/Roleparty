package app.roleparty.campaign.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageId

interface CreateCampaign {

    suspend operator fun invoke(gameMaster: AccountId, name: String, description: String, image: ImageId): Outcome<CampaignId, CampaignError>
}
