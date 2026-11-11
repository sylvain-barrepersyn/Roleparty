package app.roleparty.campaign.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.domain.port.secondary.GameSessions
import app.roleparty.campaign.domain.port.secondary.Invitations
import kotlinx.datetime.LocalDate

class CampaignStores(
    val campaigns: Campaigns,
    val invitations: Invitations,
    val gameSessions: GameSessions,
    val scheduleGameSession: suspend (CampaignId, LocalDate) -> Unit,
) {
    companion object {
        internal fun inMemory(): CampaignStores {
            val invitations = InMemoryInvitations()
            val gameSessions = InMemoryGameSessions()
            return CampaignStores(InMemoryCampaigns(invitations), invitations, gameSessions, gameSessions::schedule)
        }
    }
}
