package app.roleparty.campaign.agenda.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId

sealed interface AgendaError {

    data class CampaignNotFound(val campaignId: CampaignId) : AgendaError

    data object GameMasterRequired : AgendaError

    data object ParticipantRequired : AgendaError

    data object DateInThePast : AgendaError

    data object BlankName : AgendaError

    data class GameSessionNotFound(val gameSessionId: GameSessionId) : AgendaError

    data object GameSessionInThePast : AgendaError

    data object OthersPresenceForbidden : AgendaError

    data class ParticipantNotFound(val accountId: AccountId) : AgendaError

    data object GameMasterCannotBeAbsent : AgendaError
}
