package app.roleparty.campaign.agenda.contract.web

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
enum class PresenceName { ABSENT, IN_PERSON_WITH_COMPUTER, IN_PERSON_WITHOUT_COMPUTER, REMOTE }

@Serializable
data class GameSessionSchedulingRequestBody(val date: LocalDate)

@Serializable
data class GameSessionScheduledResponseBody(val id: GameSessionId)

@Serializable
data class GameSessionRenamingRequestBody(val name: String)

@Serializable
data class PresenceRequestBody(val presence: PresenceName)

@Serializable
data class DigitalGamingTableRequestBody(val available: Boolean)

@Serializable
data class PresenceBody(val accountId: AccountId, val presence: PresenceName)

@Serializable
data class GameSessionResponseBody(
    val id: GameSessionId,
    val date: LocalDate,
    val name: String?,
    val digitalGamingTable: Boolean = false,
    val presences: List<PresenceBody> = emptyList(),
)

@Serializable
data class ParticipantBody(val accountId: AccountId, val gameMaster: Boolean, val characterNames: List<String>)

@Serializable
data class AgendaResponseBody(val requester: AccountId, val participants: List<ParticipantBody>, val gameSessions: List<GameSessionResponseBody>)

@Serializable
data class AgendaPreferenceRequestBody(val presence: PresenceName, val digitalGamingTable: Boolean)

@Serializable
data class AgendaPreferenceResponseBody(val gameMaster: Boolean, val presence: PresenceName, val digitalGamingTable: Boolean)
