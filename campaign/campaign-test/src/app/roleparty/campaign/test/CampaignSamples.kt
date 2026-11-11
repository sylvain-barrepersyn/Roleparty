package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.media.commons.ImageId

object CampaignSamples {

    fun campaign(gameMaster: AccountId = AccountId.generate()) =
        Campaign(CampaignId.generate(), "The Lost Mine", "Goblins and caves.", ImageId.generate(), gameMaster)
}
