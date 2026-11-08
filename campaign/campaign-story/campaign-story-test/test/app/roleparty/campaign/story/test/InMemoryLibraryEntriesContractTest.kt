package app.roleparty.campaign.story.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId

class InMemoryLibraryEntriesContractTest : LibraryEntriesContractTest({
    val libraryEntries = InMemoryLibraryEntries()
    LibraryEntriesContractSubject(libraryEntries, givenCampaign = { CampaignId.generate() }) { campaignId ->
        LibraryEntryId.generate().also { libraryEntries.given(it, campaignId) }
    }
})
