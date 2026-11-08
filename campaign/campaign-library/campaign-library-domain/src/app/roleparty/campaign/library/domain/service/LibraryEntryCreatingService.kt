package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.primary.CreateLibraryEntry
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries
import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageId

internal class LibraryEntryCreatingService(
    private val libraryEntries: LibraryEntries,
    private val gameMaster: GameMasterCheckingService,
) : CreateLibraryEntry {

    override suspend fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        metadata: LibraryEntry.Metadata,
        image: ImageId,
    ): Outcome<LibraryEntryId, LibraryError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }
        if (metadata.title.isBlank()) return Outcome.Failure(LibraryError.BlankTitle)

        val libraryEntry = LibraryEntry(LibraryEntryId.generate(), campaignId, metadata.trimmed(), image)
        libraryEntries.save(libraryEntry)
        return Outcome.Success(libraryEntry.id)
    }
}

internal fun LibraryEntry.Metadata.trimmed() = LibraryEntry.Metadata(title.trim(), description.trim())
