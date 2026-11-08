package app.roleparty.campaign.agenda.infrastructure

import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.agenda.test.CampaignsContractSubject
import app.roleparty.campaign.agenda.test.CampaignsContractTest

class InfrastructuredCampaignsContractTest : CampaignsContractTest({
    CampaignsContractSubject(InfrastructuredCampaigns(TestPostgres.database)) { TestPostgres.givenCampaign(it) }
})
