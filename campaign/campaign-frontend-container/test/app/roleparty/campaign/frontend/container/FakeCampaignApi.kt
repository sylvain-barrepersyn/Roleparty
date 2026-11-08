package app.roleparty.campaign.frontend.container

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.CampaignResponseBody
import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId

internal class FakeCampaignApi : CampaignApi {

    val campaignId: CampaignId = CampaignId.generate()
    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null

    var mastered = listOf(CampaignOverviewResponseBody(CampaignId.generate(), "Curse of Strahd", ImageId.generate(), 4))
    var played = listOf(CampaignOverviewResponseBody(CampaignId.generate(), "Tomb of Annihilation", ImageId.generate(), 3))
    val campaign = CampaignResponseBody(campaignId, "Curse of Strahd", "Barovia awaits.", ImageId.generate(), AccountId.generate(), listOf(AccountId.generate()))
    val invitations = mutableListOf(InvitationResponseBody("ABC-DEF-GHJ-KLM", used = true))
    val createdCampaignId: CampaignId = CampaignId.generate()

    private fun <T> answer(call: String, value: () -> T): Outcome<T, BackendFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value())
    }

    override suspend fun campaigns(participation: Participation) =
        answer("campaigns $participation") { if (participation == Participation.GAME_MASTER) mastered else played }

    override suspend fun campaign(campaignId: CampaignId) = answer("campaign") { campaign }

    override suspend fun createCampaign(name: String, description: String, imageId: ImageId) =
        answer("create campaign $name|$description") { createdCampaignId }

    override suspend fun joinCampaign(code: String) = answer("join $code") { campaignId }

    override suspend fun invitations(campaignId: CampaignId) = answer("invitations") { invitations.toList() }

    override suspend fun generateInvitation(campaignId: CampaignId) = answer("generate invitation") { InvitationResponseBody("NPQ-RST-UVW-XYZ", used = false) }
}
