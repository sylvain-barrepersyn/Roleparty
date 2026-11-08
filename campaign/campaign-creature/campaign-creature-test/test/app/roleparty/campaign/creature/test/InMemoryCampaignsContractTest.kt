package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryCampaignsContractTest : CampaignsContractTest({
    val campaigns = InMemoryCampaigns()
    CampaignsContractSubject(campaigns) { gameMaster -> CampaignId.generate().also { campaigns.given(it, gameMaster) } }
})
