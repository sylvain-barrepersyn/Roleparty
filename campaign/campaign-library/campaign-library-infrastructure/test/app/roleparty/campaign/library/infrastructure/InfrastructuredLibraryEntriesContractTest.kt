package app.roleparty.campaign.library.infrastructure

import app.roleparty.campaign.library.infrastructure.adapter.secondary.InfrastructuredLibraryEntries
import app.roleparty.campaign.library.test.LibraryEntriesContractSubject
import app.roleparty.campaign.library.test.LibraryEntriesContractTest

class InfrastructuredLibraryEntriesContractTest : LibraryEntriesContractTest({
    LibraryEntriesContractSubject(InfrastructuredLibraryEntries(TestPostgres.database)) { TestPostgres.givenCampaign() }
})
