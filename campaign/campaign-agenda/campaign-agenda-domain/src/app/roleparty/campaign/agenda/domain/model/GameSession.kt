package app.roleparty.campaign.agenda.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import kotlinx.datetime.LocalDate

data class GameSession(
    val id: GameSessionId,
    val campaignId: CampaignId,
    val date: LocalDate,
    val name: String? = null,
    val digitalGamingTable: Boolean = false,
    val presences: Map<AccountId, Presence> = emptyMap(),
)
