package app.roleparty.campaign.story.infrastructure

import app.roleparty.campaign.story.infrastructure.adapter.secondary.InfrastructuredLibraryEntries
import app.roleparty.campaign.story.test.LibraryEntriesContractSubject
import app.roleparty.campaign.story.test.LibraryEntriesContractTest

class InfrastructuredLibraryEntriesContractTest : LibraryEntriesContractTest({
    LibraryEntriesContractSubject(
        InfrastructuredLibraryEntries(TestPostgres.database),
        givenCampaign = { TestPostgres.givenCampaign() },
        givenLibraryEntry = { TestPostgres.givenLibraryEntry(it) },
    )
})
