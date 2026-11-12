package app.roleparty.campaign.tabletop.contract.web

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.media.commons.ImageId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TabletopStartingRequestBody(val gameSessionId: GameSessionId)

@Serializable
data class StartedGameSessionBody(val id: GameSessionId, val date: LocalDate, val name: String? = null)

@Serializable
data class AttendeeBody(
    val accountId: AccountId,
    val gameMaster: Boolean,
    val characterName: String? = null,
    val imageId: ImageId? = null,
    val camera: Boolean = false,
    val microphone: Boolean = false,
)

@Serializable
data class TabletopResponseBody(val gameSession: StartedGameSessionBody? = null, val attendees: List<AttendeeBody> = emptyList())

@Serializable
sealed interface TabletopEventBody {

    @Serializable
    @SerialName("snapshot")
    data class Snapshot(val tabletop: TabletopResponseBody) : TabletopEventBody

    @Serializable
    @SerialName("game-session-started")
    data class GameSessionStarted(val gameSession: StartedGameSessionBody) : TabletopEventBody

    @Serializable
    @SerialName("attendees-changed")
    data class AttendeesChanged(val attendees: List<AttendeeBody>) : TabletopEventBody
}
