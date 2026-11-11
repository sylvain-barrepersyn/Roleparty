package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.Agenda
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.port.primary.FindAgenda
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.agenda.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

internal class AgendaFindingService(
    private val gameSessions: GameSessions,
    private val campaigns: Campaigns,
    private val playerCharacters: PlayerCharacters,
) : FindAgenda {

    override suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<Agenda, AgendaError> {
        val participants = campaigns.participantsOf(campaignId) ?: return Outcome.Failure(AgendaError.CampaignNotFound(campaignId))
        if (requester !in participants) return Outcome.Failure(AgendaError.ParticipantRequired)

        val names = playerCharacters.namesByPlayer(campaignId)
        val players = participants.players
            .map { Agenda.Participant(it, gameMaster = false, characterNames = names[it].orEmpty()) }
            .sortedWith(compareBy({ it.characterNames.isEmpty() }, { it.characterNames.joinToString().lowercase() }, { it.accountId.toString() }))
        val gameMaster = Agenda.Participant(participants.gameMaster, gameMaster = true, characterNames = emptyList())
        return Outcome.Success(Agenda(listOf(gameMaster) + players, gameSessions.ofCampaign(campaignId)))
    }
}
