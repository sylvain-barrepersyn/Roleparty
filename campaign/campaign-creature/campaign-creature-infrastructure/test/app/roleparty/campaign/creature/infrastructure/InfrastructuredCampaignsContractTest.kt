package app.roleparty.campaign.creature.infrastructure

import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.creature.test.CampaignsContractSubject
import app.roleparty.campaign.creature.test.CampaignsContractTest

class InfrastructuredCampaignsContractTest : CampaignsContractTest({
    CampaignsContractSubject(InfrastructuredCampaigns(TestPostgres.database)) { TestPostgres.givenCampaign(it) }
})
