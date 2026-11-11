package app.roleparty.campaign.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import kotlinx.datetime.LocalDate

interface GameSessions {

    suspend fun nextDates(campaignIds: Collection<CampaignId>, from: LocalDate): Map<CampaignId, LocalDate>
}
