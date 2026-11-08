package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.test.LibraryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe

class LibraryEntryCreatingServiceTest : LibraryBehaviorTest() {

    init {
        Given("a campaign") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val image = ImageId.generate()

            When("its game master adds an entry to the library") {
                val id = fixtures.createLibraryEntry(gameMaster, campaignId, LibraryEntry.Metadata(" Cave ", "Dark. "), image).shouldBeSuccess()

                Then("the entry should be created in the campaign, trimmed") {
                    fixtures.libraryEntry(id) shouldBe LibraryEntry(id, campaignId, LibraryEntry.Metadata("Cave", "Dark."), image)
                }
            }

            Then("someone else, a blank title or an unknown campaign should be refused") {
                fixtures.createLibraryEntry(AccountId.generate(), campaignId, LibraryEntry.Metadata("Cave", ""), image) shouldBeFailure LibraryError.GameMasterRequired
                fixtures.createLibraryEntry(gameMaster, campaignId, LibraryEntry.Metadata(" ", ""), image) shouldBeFailure LibraryError.BlankTitle
                val unknown = CampaignId.generate()
                fixtures.createLibraryEntry(gameMaster, unknown, LibraryEntry.Metadata("Cave", ""), image) shouldBeFailure LibraryError.CampaignNotFound(unknown)
            }
        }
    }
}
