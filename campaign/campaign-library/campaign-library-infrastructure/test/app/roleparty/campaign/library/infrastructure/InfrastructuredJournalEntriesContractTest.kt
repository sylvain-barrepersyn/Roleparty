package app.roleparty.campaign.library.infrastructure

import app.roleparty.campaign.library.infrastructure.adapter.secondary.InfrastructuredJournalEntries
import app.roleparty.campaign.library.test.JournalEntriesContractSubject
import app.roleparty.campaign.library.test.JournalEntriesContractTest

class InfrastructuredJournalEntriesContractTest : JournalEntriesContractTest({
    JournalEntriesContractSubject(InfrastructuredJournalEntries(TestPostgres.database)) { TestPostgres.givenPlayerCharacter() }
})
