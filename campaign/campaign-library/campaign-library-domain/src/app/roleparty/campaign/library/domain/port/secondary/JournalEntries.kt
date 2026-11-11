package app.roleparty.campaign.library.domain.port.secondary

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry

interface JournalEntries {

    suspend fun save(journalEntry: JournalEntry)

    suspend fun findById(journalEntryId: JournalEntryId): JournalEntry?

    suspend fun ofPlayerCharacter(playerCharacterId: PlayerCharacterId): List<JournalEntry>
}
