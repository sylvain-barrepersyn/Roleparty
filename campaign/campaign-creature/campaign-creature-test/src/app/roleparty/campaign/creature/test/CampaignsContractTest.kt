package app.roleparty.campaign.creature.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.domain.port.secondary.Campaigns
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class CampaignsContractSubject(val campaigns: Campaigns, val givenCampaign: suspend (AccountId, Set<AccountId>) -> CampaignId)

abstract class CampaignsContractTest(createSubject: () -> CampaignsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("a campaign") {
            val gameMaster = AccountId.generate()
            val players = setOf(AccountId.generate(), AccountId.generate())
            val campaignId = subject.givenCampaign(gameMaster, players)

            Then("its game master should be found, and nothing for an unknown campaign") {
                subject.campaigns.gameMasterOf(campaignId) shouldBe gameMaster
                subject.campaigns.gameMasterOf(CampaignId.generate()).shouldBeNull()
            }

            Then("its players should be found, and none for an unknown campaign") {
                subject.campaigns.playersOf(campaignId) shouldBe players
                subject.campaigns.playersOf(CampaignId.generate()) shouldBe emptySet()
            }
        }
    }
}
