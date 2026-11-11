package app.roleparty.campaign.library.test

import app.roleparty.campaign.creature.commons.PlayerCharacterId

class InMemoryJournalEntriesContractTest : JournalEntriesContractTest({
    JournalEntriesContractSubject(InMemoryJournalEntries()) { PlayerCharacterId.generate() }
})
