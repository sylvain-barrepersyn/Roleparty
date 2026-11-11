package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.domain.port.primary.ChangePresence
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

internal class PresenceChangingService(
    private val gameSessions: GameSessions,
    private val campaigns: Campaigns,
    private val clock: Clock,
    private val timeZone: TimeZone,
) : ChangePresence {

    override suspend fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        gameSessionId: GameSessionId,
        participant: AccountId,
        presence: Presence,
    ): Outcome<Unit, AgendaError> {
        val participants = campaigns.participantsOf(campaignId) ?: return Outcome.Failure(AgendaError.CampaignNotFound(campaignId))
        if (requester !in participants) return Outcome.Failure(AgendaError.ParticipantRequired)
        if (requester != participants.gameMaster && requester != participant) return Outcome.Failure(AgendaError.OthersPresenceForbidden)
        if (participant !in participants) return Outcome.Failure(AgendaError.ParticipantNotFound(participant))
        if (participant == participants.gameMaster && presence == Presence.ABSENT) return Outcome.Failure(AgendaError.GameMasterCannotBeAbsent)

        val gameSession = gameSessions.findById(gameSessionId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(AgendaError.GameSessionNotFound(gameSessionId))
        if (gameSession.date < clock.todayIn(timeZone)) return Outcome.Failure(AgendaError.GameSessionInThePast)

        gameSessions.save(gameSession.copy(presences = gameSession.presences + (participant to presence)))
        return Outcome.success()
    }
}
