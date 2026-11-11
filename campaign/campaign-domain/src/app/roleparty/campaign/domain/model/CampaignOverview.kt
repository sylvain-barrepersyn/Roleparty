package app.roleparty.campaign.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.media.commons.ImageId
import kotlinx.datetime.LocalDate

data class CampaignOverview(
    val id: CampaignId,
    val name: String,
    val image: ImageId,
    val gameMaster: AccountId,
    val players: Set<AccountId>,
    val nextSession: LocalDate? = null,
)
