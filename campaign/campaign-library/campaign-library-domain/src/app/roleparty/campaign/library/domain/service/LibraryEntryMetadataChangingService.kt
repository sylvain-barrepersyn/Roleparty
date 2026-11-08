package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.primary.ChangeLibraryEntryMetadata
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries
import app.roleparty.core.commons.Outcome

internal class LibraryEntryMetadataChangingService(
    private val libraryEntries: LibraryEntries,
    private val gameMaster: GameMasterCheckingService,
) : ChangeLibraryEntryMetadata {

    override suspend fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        libraryEntryId: LibraryEntryId,
        metadata: LibraryEntry.Metadata,
    ): Outcome<Unit, LibraryError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        val libraryEntry = libraryEntries.findById(libraryEntryId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(LibraryError.LibraryEntryNotFound(libraryEntryId))
        if (metadata.title.isBlank()) return Outcome.Failure(LibraryError.BlankTitle)

        libraryEntries.save(libraryEntry.copy(metadata = metadata.trimmed()))
        return Outcome.success()
    }
}
