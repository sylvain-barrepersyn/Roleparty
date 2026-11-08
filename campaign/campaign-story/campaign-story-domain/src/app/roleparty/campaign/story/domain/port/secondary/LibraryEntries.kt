package app.roleparty.campaign.story.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId

interface LibraryEntries {

    suspend fun campaignOf(libraryEntryId: LibraryEntryId): CampaignId?
}
