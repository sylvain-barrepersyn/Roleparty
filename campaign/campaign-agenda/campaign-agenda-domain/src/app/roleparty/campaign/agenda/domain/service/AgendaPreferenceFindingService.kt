package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.ParticipantAgendaPreference
import app.roleparty.campaign.agenda.domain.port.primary.FindAgendaPreference
import app.roleparty.campaign.agenda.domain.port.secondary.AgendaPreferences
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

internal class AgendaPreferenceFindingService(
    private val agendaPreferences: AgendaPreferences,
    private val campaigns: Campaigns,
) : FindAgendaPreference {

    override suspend fun ofRequester(requester: AccountId, campaignId: CampaignId): Outcome<ParticipantAgendaPreference, AgendaError> {
        val participants = campaigns.participantsOf(campaignId) ?: return Outcome.Failure(AgendaError.CampaignNotFound(campaignId))
        if (requester !in participants) return Outcome.Failure(AgendaError.ParticipantRequired)

        val gameMaster = requester == participants.gameMaster
        val preference = agendaPreferences.ofCampaign(campaignId)[requester] ?: AgendaPreference.defaultFor(gameMaster)
        return Outcome.Success(ParticipantAgendaPreference(gameMaster = gameMaster, preference = preference))
    }
}
