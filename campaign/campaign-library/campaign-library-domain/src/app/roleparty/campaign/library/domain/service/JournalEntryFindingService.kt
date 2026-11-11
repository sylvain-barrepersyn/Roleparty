package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.primary.FindJournalEntries
import app.roleparty.campaign.library.domain.port.secondary.JournalEntries
import app.roleparty.core.commons.Outcome

internal class JournalEntryFindingService(
    private val journalEntries: JournalEntries,
    private val owner: PlayerCharacterOwnerCheckingService,
) : FindJournalEntries {

    override suspend fun ofPlayerCharacter(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<List<JournalEntry>, LibraryError> {
        owner.check(requester, playerCharacterId)?.let { return Outcome.Failure(it) }

        return Outcome.Success(journalEntries.ofPlayerCharacter(playerCharacterId).reversed())
    }
}
