package app.roleparty.campaign.library.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.core.commons.Outcome

interface CreateJournalEntry {

    suspend operator fun invoke(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<JournalEntryId, LibraryError>
}
