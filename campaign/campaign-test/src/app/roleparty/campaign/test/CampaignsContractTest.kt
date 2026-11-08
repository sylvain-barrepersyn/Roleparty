package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

abstract class CampaignsContractTest(createStores: () -> CampaignStores) : FixturedBehaviorTest() {

    private val stores = createStores()
    private val campaigns = stores.campaigns

    init {
        Given("an unknown campaign") {
            Then("it should find nothing") {
                campaigns.findById(CampaignId.generate()).shouldBeNull()
            }
        }

        Given("a created campaign") {
            val campaign = CampaignSamples.campaign()
            campaigns.create(campaign)

            Then("it should be found, and listed as mastered by its game master") {
                campaigns.findById(campaign.id) shouldBe campaign
                campaigns.masteredBy(campaign.gameMaster) shouldBe listOf(CampaignOverview(campaign.id, campaign.name, campaign.image, campaign.gameMaster, emptySet()))
                campaigns.playedBy(campaign.gameMaster).shouldBeEmpty()
            }

            When("a player joins it with an unused invitation") {
                val invitation = Invitation(Invitation.Code.generate(), campaign.id)
                stores.invitations.create(invitation)
                val player = AccountId.generate()
                val added = campaigns.addPlayer(campaign.id, player, invitation.code)

                Then("the player should be added and the invitation used") {
                    added shouldBe true
                    campaigns.findById(campaign.id)?.players shouldBe setOf(player)
                    campaigns.playedBy(player).map { it.id } shouldBe listOf(campaign.id)
                    stores.invitations.findByCode(invitation.code)?.used shouldBe true
                }

                Then("the same invitation should not add another player") {
                    val other = AccountId.generate()
                    campaigns.addPlayer(campaign.id, other, invitation.code) shouldBe false
                    campaigns.findById(campaign.id)?.players shouldBe setOf(player)
                }
            }
        }
    }
}
