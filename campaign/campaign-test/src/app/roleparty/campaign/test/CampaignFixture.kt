package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.CampaignModule
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.datetime.LocalDate

class CampaignFixture {

    private val invitations = InMemoryInvitations()
    private val campaigns = InMemoryCampaigns(invitations)
    private val gameSessions = InMemoryGameSessions()

    val module = CampaignModule(campaigns, invitations, gameSessions)

    val createCampaign = module.createCampaign
    val findCampaigns = module.findCampaigns
    val inviteToCampaign = module.inviteToCampaign
    val joinCampaign = module.joinCampaign

    suspend fun `given a campaign`(gameMaster: AccountId = AccountId.generate()): CampaignId =
        createCampaign(gameMaster, "The Lost Mine", "Goblins and caves.", ImageId.generate()).shouldBeSuccess()

    suspend fun `given a campaign named`(name: String, gameMaster: AccountId): CampaignId =
        createCampaign(gameMaster, name, "", ImageId.generate()).shouldBeSuccess()

    fun `given a game session of`(campaignId: CampaignId, date: LocalDate) = gameSessions.schedule(campaignId, date)

    suspend fun `given a player of`(campaignId: CampaignId, player: AccountId = AccountId.generate()): AccountId {
        val code = inviteToCampaign.generateCode(campaign(campaignId).gameMaster, campaignId).shouldBeSuccess()
        joinCampaign(player, code).shouldBeSuccess()
        return player
    }

    suspend fun campaign(campaignId: CampaignId): Campaign = campaigns.findById(campaignId).shouldNotBeNull()
}
