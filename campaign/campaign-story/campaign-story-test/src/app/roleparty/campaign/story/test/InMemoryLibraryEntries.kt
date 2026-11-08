package app.roleparty.campaign.story.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.domain.port.secondary.LibraryEntries

internal class InMemoryLibraryEntries : LibraryEntries {

    private val campaigns = mutableMapOf<LibraryEntryId, CampaignId>()

    override suspend fun campaignOf(libraryEntryId: LibraryEntryId) = campaigns[libraryEntryId]

    fun given(libraryEntryId: LibraryEntryId, campaignId: CampaignId) {
        campaigns[libraryEntryId] = campaignId
    }
}
