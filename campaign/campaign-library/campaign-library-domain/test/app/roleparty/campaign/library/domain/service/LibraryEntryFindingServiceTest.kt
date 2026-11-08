package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.test.LibraryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class LibraryEntryFindingServiceTest : LibraryBehaviorTest() {

    init {
        Given("a campaign with two library entries") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val first = fixtures.`given a library entry`(gameMaster, campaignId, "Cave")
            val second = fixtures.`given a library entry`(gameMaster, campaignId, "Forest")

            Then("the game master should list them, and nobody else") {
                fixtures.findLibraryEntries.ofCampaign(gameMaster, campaignId).shouldBeSuccess().map { it.id } shouldBe listOf(first, second)
                fixtures.findLibraryEntries.ofCampaign(AccountId.generate(), campaignId) shouldBeFailure LibraryError.GameMasterRequired
            }
        }
    }
}
