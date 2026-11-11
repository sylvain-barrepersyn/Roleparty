package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryCampaignsContractTest : CampaignsContractTest({
    val campaigns = InMemoryCampaigns()
    CampaignsContractSubject(campaigns) { gameMaster, players -> CampaignId.generate().also { campaigns.given(it, gameMaster, players) } }
})
