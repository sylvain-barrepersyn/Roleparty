package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.port.primary.CreateCampaign
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageId

internal class CampaignCreatingService(private val campaigns: Campaigns) : CreateCampaign {

    override suspend fun invoke(gameMaster: AccountId, name: String, description: String, image: ImageId): Outcome<CampaignId, CampaignError> {
        if (name.isBlank()) return Outcome.Failure(CampaignError.BlankName)

        val campaign = Campaign(CampaignId.generate(), name.trim(), description.trim(), image, gameMaster)
        campaigns.create(campaign)
        return Outcome.Success(campaign.id)
    }
}
