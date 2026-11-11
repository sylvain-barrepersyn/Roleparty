package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.domain.port.primary.ChangeAgendaPreference
import app.roleparty.campaign.agenda.domain.port.secondary.AgendaPreferences
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

internal class AgendaPreferenceChangingService(
    private val agendaPreferences: AgendaPreferences,
    private val campaigns: Campaigns,
) : ChangeAgendaPreference {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, preference: AgendaPreference): Outcome<Unit, AgendaError> {
        val participants = campaigns.participantsOf(campaignId) ?: return Outcome.Failure(AgendaError.CampaignNotFound(campaignId))
        if (requester !in participants) return Outcome.Failure(AgendaError.ParticipantRequired)
        val gameMaster = requester == participants.gameMaster
        if (gameMaster && preference.presence == Presence.ABSENT) return Outcome.Failure(AgendaError.GameMasterCannotBeAbsent)
        if (!gameMaster && preference.digitalGamingTable) return Outcome.Failure(AgendaError.GameMasterRequired)

        agendaPreferences.save(campaignId, requester, preference)
        return Outcome.success()
    }
}
