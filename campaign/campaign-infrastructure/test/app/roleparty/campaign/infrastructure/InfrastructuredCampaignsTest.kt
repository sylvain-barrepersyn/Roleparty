package app.roleparty.campaign.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.test.CampaignSamples
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class InfrastructuredCampaignsTest : FunSpec({

    test("creating a campaign and adding a player should enqueue their public events") {
        val stores = TestPostgres.stores()
        val campaign = CampaignSamples.campaign()
        stores.campaigns.create(campaign)
        val invitation = Invitation(Invitation.Code.generate(), campaign.id)
        stores.invitations.create(invitation)
        val player = AccountId.generate()

        stores.campaigns.addPlayer(campaign.id, player, invitation.code) shouldBe true

        val payloads = TestPostgres.sql("select exchange || ' ' || payload from technical.event")
        payloads.filter { campaign.id.toString() in it }.map { it.substringBefore(' ') } shouldContainAll
            listOf("campaign.campaign-created", "campaign.player-joined-campaign")
    }

    test("an invitation of another campaign should not add a player") {
        val stores = TestPostgres.stores()
        val campaign = CampaignSamples.campaign().also { stores.campaigns.create(it) }
        val other = CampaignSamples.campaign().also { stores.campaigns.create(it) }
        val invitation = Invitation(Invitation.Code.generate(), other.id).also { stores.invitations.create(it) }

        stores.campaigns.addPlayer(campaign.id, AccountId.generate(), invitation.code) shouldBe false
        stores.campaigns.findById(campaign.id)?.players shouldBe emptySet()
    }
})
