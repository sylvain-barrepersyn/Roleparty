package app.roleparty.campaign.library.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class LibraryEntriesContractSubject(val libraryEntries: LibraryEntries, val givenCampaign: suspend () -> CampaignId)

abstract class LibraryEntriesContractTest(createSubject: () -> LibraryEntriesContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val libraryEntries = subject.libraryEntries

    init {
        Given("library entries of two campaigns") {
            val campaignId = subject.givenCampaign()
            val first = LibraryEntry(LibraryEntryId.generate(), campaignId, LibraryEntry.Metadata("Cave", "Dark."), ImageId.generate())
            val second = LibraryEntry(LibraryEntryId.generate(), campaignId, LibraryEntry.Metadata("Forest", ""), ImageId.generate())
            val other = LibraryEntry(LibraryEntryId.generate(), subject.givenCampaign(), LibraryEntry.Metadata("Town", ""), ImageId.generate())
            listOf(first, second, other).forEach { libraryEntries.save(it) }

            Then("they should be found by id, and listed by campaign in creation order") {
                libraryEntries.findById(first.id) shouldBe first
                libraryEntries.findById(LibraryEntryId.generate()).shouldBeNull()
                libraryEntries.ofCampaign(campaignId) shouldBe listOf(first, second)
            }

            When("an entry is saved with new metadata") {
                val renamed = first.copy(metadata = LibraryEntry.Metadata("Deep cave", "Darker."))
                libraryEntries.save(renamed)

                Then("it should be found with the new metadata") {
                    libraryEntries.findById(first.id) shouldBe renamed
                }
            }

        }
    }
}
