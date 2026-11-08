package app.roleparty.campaign.agenda.contract.web

import app.roleparty.campaign.agenda.commons.GameSessionId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class GameSessionSchedulingRequestBody(val date: LocalDate)

@Serializable
data class GameSessionScheduledResponseBody(val id: GameSessionId)

@Serializable
data class GameSessionRenamingRequestBody(val name: String)

@Serializable
data class GameSessionResponseBody(val id: GameSessionId, val date: LocalDate, val name: String?)
