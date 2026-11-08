package app.roleparty.campaign.story.infrastructure

import app.roleparty.campaign.story.infrastructure.adapter.secondary.InfrastructuredScenes
import app.roleparty.campaign.story.test.ScenesContractSubject
import app.roleparty.campaign.story.test.ScenesContractTest

class InfrastructuredScenesContractTest : ScenesContractTest({
    ScenesContractSubject(
        InfrastructuredScenes(TestPostgres.database),
        givenCampaign = { TestPostgres.givenCampaign() },
        givenLibraryEntry = { TestPostgres.givenLibraryEntry(it) },
    )
})
