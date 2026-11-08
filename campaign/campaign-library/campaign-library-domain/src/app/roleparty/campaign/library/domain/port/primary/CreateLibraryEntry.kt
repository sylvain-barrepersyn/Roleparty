package app.roleparty.campaign.library.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageId

interface CreateLibraryEntry {

    suspend operator fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        metadata: LibraryEntry.Metadata,
        image: ImageId,
    ): Outcome<LibraryEntryId, LibraryError>
}
