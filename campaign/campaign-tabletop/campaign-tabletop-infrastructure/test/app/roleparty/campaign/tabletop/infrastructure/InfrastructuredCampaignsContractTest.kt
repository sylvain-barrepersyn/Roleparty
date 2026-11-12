package app.roleparty.campaign.tabletop.infrastructure

import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.tabletop.test.CampaignsContractSubject
import app.roleparty.campaign.tabletop.test.CampaignsContractTest

class InfrastructuredCampaignsContractTest : CampaignsContractTest({
    CampaignsContractSubject(InfrastructuredCampaigns(TestPostgres.database)) { TestPostgres.givenCampaign(it) }
})
