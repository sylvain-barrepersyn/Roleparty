package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryCampaignsContractTest : CampaignsContractTest({
    val campaigns = InMemoryCampaigns()
    CampaignsContractSubject(campaigns) { participants -> CampaignId.generate().also { campaigns.given(it, participants) } }
})
