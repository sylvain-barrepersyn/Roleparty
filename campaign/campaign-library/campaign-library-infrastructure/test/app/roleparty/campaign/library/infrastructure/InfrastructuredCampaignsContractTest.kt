package app.roleparty.campaign.library.infrastructure

import app.roleparty.campaign.library.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.library.test.CampaignsContractSubject
import app.roleparty.campaign.library.test.CampaignsContractTest

class InfrastructuredCampaignsContractTest : CampaignsContractTest({
    CampaignsContractSubject(InfrastructuredCampaigns(TestPostgres.database)) { TestPostgres.givenCampaign(it) }
})
