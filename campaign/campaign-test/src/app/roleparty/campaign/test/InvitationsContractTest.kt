package app.roleparty.campaign.test

import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

abstract class InvitationsContractTest(createStores: () -> CampaignStores) : FixturedBehaviorTest() {

    private val stores = createStores()
    private val invitations = stores.invitations

    init {
        Given("invitations of two campaigns") {
            val campaign = CampaignSamples.campaign().also { stores.campaigns.create(it) }
            val other = CampaignSamples.campaign().also { stores.campaigns.create(it) }
            val first = Invitation(Invitation.Code.generate(), campaign.id)
            val second = Invitation(Invitation.Code.generate(), campaign.id)
            listOf(first, second, Invitation(Invitation.Code.generate(), other.id)).forEach { invitations.create(it) }

            Then("they should be found by code, and listed by campaign") {
                invitations.findByCode(first.code) shouldBe first
                invitations.findByCode(Invitation.Code.generate()).shouldBeNull()
                invitations.ofCampaign(campaign.id).toSet() shouldBe setOf(first, second)
            }
        }
    }
}
