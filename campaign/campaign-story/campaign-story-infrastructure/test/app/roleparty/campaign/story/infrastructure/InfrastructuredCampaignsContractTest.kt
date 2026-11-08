package app.roleparty.campaign.story.infrastructure

import app.roleparty.campaign.story.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.story.test.CampaignsContractSubject
import app.roleparty.campaign.story.test.CampaignsContractTest

class InfrastructuredCampaignsContractTest : CampaignsContractTest({
    CampaignsContractSubject(InfrastructuredCampaigns(TestPostgres.database)) { TestPostgres.givenCampaign(it) }
})
