package app.roleparty.campaign.tabletop.infrastructure

import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredTabletops
import app.roleparty.campaign.tabletop.test.TabletopsContractSubject
import app.roleparty.campaign.tabletop.test.TabletopsContractTest

class InfrastructuredTabletopsContractTest : TabletopsContractTest({
    TabletopsContractSubject(
        tabletops = InfrastructuredTabletops(TestPostgres.database),
        givenCampaign = { TestPostgres.givenCampaign() },
        givenGameSession = { TestPostgres.givenGameSession(it).id },
    )
})
