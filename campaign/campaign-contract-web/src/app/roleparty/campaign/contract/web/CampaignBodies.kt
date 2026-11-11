package app.roleparty.campaign.contract.web

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.media.commons.ImageId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
enum class Participation { GAME_MASTER, PLAYER }

@Serializable
data class CampaignCreationRequestBody(val name: String, val description: String, val imageId: ImageId)

@Serializable
data class CampaignCreatedResponseBody(val id: CampaignId)

@Serializable
data class CampaignOverviewResponseBody(val id: CampaignId, val name: String, val imageId: ImageId, val players: Int, val nextSession: LocalDate? = null)

@Serializable
data class CampaignResponseBody(
    val id: CampaignId,
    val name: String,
    val description: String,
    val imageId: ImageId,
    val gameMaster: AccountId,
    val players: List<AccountId>,
)

@Serializable
data class InvitationResponseBody(val code: String, val used: Boolean)

@Serializable
data class CampaignJoiningRequestBody(val code: String)

@Serializable
data class CampaignJoinedResponseBody(val campaignId: CampaignId)
