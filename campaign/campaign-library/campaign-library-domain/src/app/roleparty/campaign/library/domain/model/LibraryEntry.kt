package app.roleparty.campaign.library.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.media.commons.ImageId

data class LibraryEntry(
    val id: LibraryEntryId,
    val campaignId: CampaignId,
    val metadata: Metadata,
    val image: ImageId,
) {

    data class Metadata(val title: String, val description: String)
}
