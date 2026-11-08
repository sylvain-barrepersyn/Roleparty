package app.roleparty.campaign.agenda.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.CampaignParticipants
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class CampaignsContractSubject(val campaigns: Campaigns, val givenCampaign: suspend (CampaignParticipants) -> CampaignId)

abstract class CampaignsContractTest(createSubject: () -> CampaignsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("a campaign with players") {
            val participants = CampaignParticipants(AccountId.generate(), setOf(AccountId.generate(), AccountId.generate()))
            val campaignId = subject.givenCampaign(participants)

            Then("its participants should be found") {
                subject.campaigns.participantsOf(campaignId) shouldBe participants
            }
        }

        Given("an unknown campaign") {
            Then("it should have no participants") {
                subject.campaigns.participantsOf(CampaignId.generate()).shouldBeNull()
            }
        }
    }
}
