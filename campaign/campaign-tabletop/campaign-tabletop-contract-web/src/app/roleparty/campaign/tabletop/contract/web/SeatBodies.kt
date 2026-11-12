package app.roleparty.campaign.tabletop.contract.web

import app.roleparty.account.commons.AccountId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IceServerBody(val urls: List<String>, val username: String? = null, val credential: String? = null)

@Serializable
data class SeatResponseBody(val accountId: AccountId, val gameMaster: Boolean, val iceServers: List<IceServerBody> = emptyList())

@Serializable
data class PeerBody(val accountId: AccountId, val gameMaster: Boolean)

@Serializable
sealed interface SignalBody {

    @Serializable
    @SerialName("offer")
    data class Offer(val sdp: String) : SignalBody

    @Serializable
    @SerialName("answer")
    data class Answer(val sdp: String) : SignalBody

    @Serializable
    @SerialName("ice-candidate")
    data class IceCandidate(val candidate: String, val sdpMid: String, val sdpMLineIndex: Int) : SignalBody
}

@Serializable
sealed interface SeatCommandBody {

    @Serializable
    @SerialName("signal")
    data class Signal(val to: AccountId, val signal: SignalBody) : SeatCommandBody

    @Serializable
    @SerialName("hear-game-master")
    data class HearGameMaster(val hearing: Boolean) : SeatCommandBody

    @Serializable
    @SerialName("change-media")
    data class ChangeMedia(val camera: Boolean, val microphone: Boolean) : SeatCommandBody
}

@Serializable
sealed interface SeatEventBody {

    @Serializable
    @SerialName("welcomed")
    data class Welcomed(val peers: List<PeerBody>) : SeatEventBody

    @Serializable
    @SerialName("peer-joined")
    data class PeerJoined(val peer: PeerBody) : SeatEventBody

    @Serializable
    @SerialName("peer-left")
    data class PeerLeft(val accountId: AccountId) : SeatEventBody

    @Serializable
    @SerialName("signal")
    data class Signal(val from: AccountId, val signal: SignalBody) : SeatEventBody

    @Serializable
    @SerialName("attendees-changed")
    data class AttendeesChanged(val attendees: List<AttendeeBody>) : SeatEventBody
}
