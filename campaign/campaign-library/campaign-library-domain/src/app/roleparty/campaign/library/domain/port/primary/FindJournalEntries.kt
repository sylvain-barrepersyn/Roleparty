package app.roleparty.campaign.library.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.core.commons.Outcome

interface FindJournalEntries {

    suspend fun ofPlayerCharacter(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<List<JournalEntry>, LibraryError>
}
