package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.primary.UpdateJournalEntry
import app.roleparty.campaign.library.domain.port.secondary.JournalEntries
import app.roleparty.core.commons.Outcome

internal class JournalEntryUpdatingService(
    private val journalEntries: JournalEntries,
    private val owner: PlayerCharacterOwnerCheckingService,
) : UpdateJournalEntry {

    override suspend fun invoke(
        requester: AccountId,
        playerCharacterId: PlayerCharacterId,
        journalEntryId: JournalEntryId,
        content: JournalEntry.Content,
    ): Outcome<Unit, LibraryError> {
        owner.check(requester, playerCharacterId)?.let { return Outcome.Failure(it) }

        val journalEntry = journalEntries.findById(journalEntryId)?.takeIf { it.playerCharacterId == playerCharacterId }
            ?: return Outcome.Failure(LibraryError.JournalEntryNotFound(journalEntryId))

        journalEntries.save(journalEntry.copy(content = JournalEntry.Content(content.title.trim(), content.text, content.images.distinct())))
        return Outcome.success()
    }
}
