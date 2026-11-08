package app.roleparty.campaign.library.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.LibraryModule
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.nulls.shouldNotBeNull

class LibraryFixture {

    private val libraryEntries = InMemoryLibraryEntries()
    private val campaigns = InMemoryCampaigns()

    val module = LibraryModule(libraryEntries, campaigns)

    val createLibraryEntry = module.createLibraryEntry
    val findLibraryEntries = module.findLibraryEntries
    val changeLibraryEntryMetadata = module.changeLibraryEntryMetadata

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, gameMaster)
        return campaignId
    }

    suspend fun `given a library entry`(gameMaster: AccountId, campaignId: CampaignId, title: String = "Cave"): LibraryEntryId =
        createLibraryEntry(gameMaster, campaignId, LibraryEntry.Metadata(title, "Dark."), ImageId.generate()).shouldBeSuccess()

    suspend fun libraryEntry(libraryEntryId: LibraryEntryId) = libraryEntries.findById(libraryEntryId).shouldNotBeNull()
}
