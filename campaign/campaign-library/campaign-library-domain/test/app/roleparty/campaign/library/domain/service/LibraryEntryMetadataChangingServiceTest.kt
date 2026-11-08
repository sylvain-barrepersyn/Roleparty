package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.test.LibraryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class LibraryEntryMetadataChangingServiceTest : LibraryBehaviorTest() {

    init {
        Given("a library entry of a campaign") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val id = fixtures.`given a library entry`(gameMaster, campaignId)

            When("the game master changes its metadata") {
                fixtures.changeLibraryEntryMetadata(gameMaster, campaignId, id, LibraryEntry.Metadata("Deep cave", "Darker.")).shouldBeSuccess()

                Then("the entry should hold the new metadata") {
                    fixtures.libraryEntry(id).metadata shouldBe LibraryEntry.Metadata("Deep cave", "Darker.")
                }
            }

            When("changing it through another campaign of the same game master") {
                val otherCampaign = fixtures.`given a campaign`(gameMaster)

                Then("the entry should not be found") {
                    val outcome = fixtures.changeLibraryEntryMetadata(gameMaster, otherCampaign, id, LibraryEntry.Metadata("Other", ""))
                    outcome shouldBeFailure LibraryError.LibraryEntryNotFound(id)
                }
            }

            Then("a blank title should be refused") {
                fixtures.changeLibraryEntryMetadata(gameMaster, campaignId, id, LibraryEntry.Metadata("", "")) shouldBeFailure LibraryError.BlankTitle
            }
        }
    }
}
