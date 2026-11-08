package app.roleparty.campaign.library.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry

interface LibraryEntries {

    suspend fun save(libraryEntry: LibraryEntry)

    suspend fun findById(libraryEntryId: LibraryEntryId): LibraryEntry?

    suspend fun ofCampaign(campaignId: CampaignId): List<LibraryEntry>
}
