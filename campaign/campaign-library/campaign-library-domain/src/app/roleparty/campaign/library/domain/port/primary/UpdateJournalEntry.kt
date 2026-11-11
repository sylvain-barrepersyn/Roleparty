package app.roleparty.campaign.library.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.core.commons.Outcome

interface UpdateJournalEntry {

    suspend operator fun invoke(
        requester: AccountId,
        playerCharacterId: PlayerCharacterId,
        journalEntryId: JournalEntryId,
        content: JournalEntry.Content,
    ): Outcome<Unit, LibraryError>
}
