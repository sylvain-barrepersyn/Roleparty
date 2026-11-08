package app.roleparty.campaign.library.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.domain.port.secondary.Campaigns
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class CampaignsContractSubject(val campaigns: Campaigns, val givenCampaign: suspend (AccountId) -> CampaignId)

abstract class CampaignsContractTest(createSubject: () -> CampaignsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("a campaign") {
            val gameMaster = AccountId.generate()
            val campaignId = subject.givenCampaign(gameMaster)

            Then("its game master should be found, and nothing for an unknown campaign") {
                subject.campaigns.gameMasterOf(campaignId) shouldBe gameMaster
                subject.campaigns.gameMasterOf(CampaignId.generate()).shouldBeNull()
            }
        }
    }
}
