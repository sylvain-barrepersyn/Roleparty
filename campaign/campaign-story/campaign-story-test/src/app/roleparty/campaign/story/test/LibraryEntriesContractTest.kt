package app.roleparty.campaign.story.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.domain.port.secondary.LibraryEntries
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class LibraryEntriesContractSubject(
    val libraryEntries: LibraryEntries,
    val givenCampaign: suspend () -> CampaignId,
    val givenLibraryEntry: suspend (CampaignId) -> LibraryEntryId,
)

abstract class LibraryEntriesContractTest(createSubject: () -> LibraryEntriesContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("a library entry of a campaign") {
            val campaignId = subject.givenCampaign()
            val libraryEntryId = subject.givenLibraryEntry(campaignId)

            Then("its campaign should be found, and nothing for an unknown entry") {
                subject.libraryEntries.campaignOf(libraryEntryId) shouldBe campaignId
                subject.libraryEntries.campaignOf(LibraryEntryId.generate()).shouldBeNull()
            }
        }
    }
}
