package app.roleparty.campaign.library.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries

internal class InMemoryLibraryEntries : LibraryEntries {

    private val libraryEntries = linkedMapOf<LibraryEntryId, LibraryEntry>()

    override suspend fun save(libraryEntry: LibraryEntry) {
        libraryEntries[libraryEntry.id] = libraryEntry
    }

    override suspend fun findById(libraryEntryId: LibraryEntryId) = libraryEntries[libraryEntryId]

    override suspend fun ofCampaign(campaignId: CampaignId) = libraryEntries.values.filter { it.campaignId == campaignId }
}
