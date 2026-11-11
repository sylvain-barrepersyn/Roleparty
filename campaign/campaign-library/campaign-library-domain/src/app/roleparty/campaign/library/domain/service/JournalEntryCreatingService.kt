package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.primary.CreateJournalEntry
import app.roleparty.campaign.library.domain.port.secondary.JournalEntries
import app.roleparty.core.commons.Outcome

internal class JournalEntryCreatingService(
    private val journalEntries: JournalEntries,
    private val owner: PlayerCharacterOwnerCheckingService,
) : CreateJournalEntry {

    override suspend fun invoke(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<JournalEntryId, LibraryError> {
        owner.check(requester, playerCharacterId)?.let { return Outcome.Failure(it) }

        val journalEntry = JournalEntry(JournalEntryId.generate(), playerCharacterId, JournalEntry.Content.EMPTY)
        journalEntries.save(journalEntry)
        return Outcome.Success(journalEntry.id)
    }
}
