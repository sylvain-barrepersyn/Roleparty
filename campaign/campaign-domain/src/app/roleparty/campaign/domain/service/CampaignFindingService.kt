package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.campaign.domain.port.primary.FindCampaigns
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.domain.port.secondary.GameSessions
import kotlinx.datetime.LocalDate

internal class CampaignFindingService(
    private val campaigns: Campaigns,
    private val gameSessions: GameSessions,
    private val access: CampaignAccessCheckingService,
) : FindCampaigns {

    override suspend fun masteredBy(accountId: AccountId, today: LocalDate) = scheduled(campaigns.masteredBy(accountId), today)

    override suspend fun playedBy(accountId: AccountId, today: LocalDate) = scheduled(campaigns.playedBy(accountId), today)

    override suspend fun byId(requester: AccountId, campaignId: CampaignId) = access.asParticipant(requester, campaignId)

    private suspend fun scheduled(overviews: List<CampaignOverview>, today: LocalDate): List<CampaignOverview> {
        val nextDates = gameSessions.nextDates(overviews.map { it.id }, today)
        return overviews.map { it.copy(nextSession = nextDates[it.id]) }
            .sortedWith(compareBy<CampaignOverview, LocalDate?>(nullsLast()) { it.nextSession }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }
}
