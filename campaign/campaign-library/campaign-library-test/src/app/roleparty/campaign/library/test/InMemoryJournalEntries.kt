package app.roleparty.campaign.library.test

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.port.secondary.JournalEntries

internal class InMemoryJournalEntries : JournalEntries {

    private val journalEntries = linkedMapOf<JournalEntryId, JournalEntry>()

    override suspend fun save(journalEntry: JournalEntry) {
        journalEntries[journalEntry.id] = journalEntry
    }

    override suspend fun findById(journalEntryId: JournalEntryId) = journalEntries[journalEntryId]

    override suspend fun ofPlayerCharacter(playerCharacterId: PlayerCharacterId) =
        journalEntries.values.filter { it.playerCharacterId == playerCharacterId }
}
