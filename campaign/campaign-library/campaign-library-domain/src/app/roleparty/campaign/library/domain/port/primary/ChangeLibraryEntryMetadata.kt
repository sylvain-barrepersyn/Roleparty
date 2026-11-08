package app.roleparty.campaign.library.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.core.commons.Outcome

interface ChangeLibraryEntryMetadata {

    suspend operator fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        libraryEntryId: LibraryEntryId,
        metadata: LibraryEntry.Metadata,
    ): Outcome<Unit, LibraryError>
}
