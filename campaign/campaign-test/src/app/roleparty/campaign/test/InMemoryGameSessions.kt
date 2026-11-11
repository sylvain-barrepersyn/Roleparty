package app.roleparty.campaign.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.port.secondary.GameSessions
import kotlinx.datetime.LocalDate

internal class InMemoryGameSessions : GameSessions {

    private val dates = mutableListOf<Pair<CampaignId, LocalDate>>()

    fun schedule(campaignId: CampaignId, date: LocalDate) {
        dates += campaignId to date
    }

    override suspend fun nextDates(campaignIds: Collection<CampaignId>, from: LocalDate) =
        dates.filter { it.first in campaignIds && it.second >= from }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.min() }
}
