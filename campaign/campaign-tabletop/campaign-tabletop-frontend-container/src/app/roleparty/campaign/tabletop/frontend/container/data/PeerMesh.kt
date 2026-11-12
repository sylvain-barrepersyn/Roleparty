package app.roleparty.campaign.tabletop.frontend.container.data

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.tabletop.contract.web.SignalBody
import io.ktor.client.webrtc.TrackEvent
import io.ktor.client.webrtc.WebRtc
import io.ktor.client.webrtc.WebRtcMedia
import io.ktor.client.webrtc.WebRtcPeerConnection
import io.ktor.client.webrtc.media.getNative
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalKtorApi::class)
internal class PeerMesh(
    private val media: TabletopMedia,
    private val iceServers: List<WebRtc.IceServer>,
    private val scope: CoroutineScope,
    private val feedOf: (AccountId) -> VideoFeed,
    private val send: suspend (AccountId, SignalBody) -> Unit,
) {

    private val peers = mutableMapOf<AccountId, Peer>()

    private val mutableConnected = MutableStateFlow<Set<AccountId>>(emptySet())
    val connected: StateFlow<Set<AccountId>> = mutableConnected.asStateFlow()

    suspend fun call(peer: AccountId) {
        val connection = open(peer).connection
        val offer = connection.createOffer()
        connection.setLocalDescription(offer)
        send(peer, SignalBody.Offer(offer.sdp))
    }

    suspend fun receive(from: AccountId, signal: SignalBody) {
        when (signal) {
            is SignalBody.Offer -> {
                val peer = open(from)
                peer.describe(WebRtc.SessionDescription(WebRtc.SessionDescriptionType.OFFER, signal.sdp))
                val answer = peer.connection.createAnswer()
                peer.connection.setLocalDescription(answer)
                send(from, SignalBody.Answer(answer.sdp))
            }

            is SignalBody.Answer -> peers[from]?.describe(WebRtc.SessionDescription(WebRtc.SessionDescriptionType.ANSWER, signal.sdp))

            is SignalBody.IceCandidate -> peers[from]?.add(WebRtc.IceCandidate(signal.candidate, signal.sdpMid, signal.sdpMLineIndex))
        }
    }

    suspend fun refreshTracks() {
        peers.values.forEach { it.send(media.microphone, media.camera) }
    }

    fun hangUp(peer: AccountId) {
        val left = peers.remove(peer) ?: return
        feedOf(peer).release()
        left.close()
        mutableConnected.update { it - peer }
    }

    fun hangUpAll() {
        peers.keys.toList().forEach(::hangUp)
    }

    private suspend fun open(peer: AccountId): Peer {
        hangUp(peer)
        val connection = media.client.createPeerConnection { iceServers = this@PeerMesh.iceServers }
        val audio = connection.addTrack(media.silence)
        val video = connection.addTrack(media.blank)
        val watching = scope.launch {
            launch { connection.iceCandidates.collect { send(peer, SignalBody.IceCandidate(it.candidate, it.sdpMid, it.sdpMLineIndex)) } }
            launch {
                connection.state.collect { state ->
                    mutableConnected.update { if (state == WebRtc.ConnectionState.CONNECTED) it + peer else it - peer }
                }
            }
            launch {
                connection.trackEvents.collect { event ->
                    if (event is TrackEvent.Add) (event.track as? WebRtcMedia.VideoTrack)?.let { feedOf(peer).show(it.getNative()) }
                }
            }
        }
        return Peer(connection, audio, video, watching).also {
            it.send(media.microphone, media.camera)
            peers[peer] = it
        }
    }

    private class Peer(
        val connection: WebRtcPeerConnection,
        private val audio: WebRtc.RtpSender,
        private val video: WebRtc.RtpSender,
        private val watching: Job,
    ) {

        private var described = false
        private val pendingCandidates = mutableListOf<WebRtc.IceCandidate>()

        suspend fun send(microphone: WebRtcMedia.AudioTrack?, camera: WebRtcMedia.VideoTrack?) {
            audio.replaceTrack(microphone)
            video.replaceTrack(camera)
        }

        suspend fun describe(description: WebRtc.SessionDescription) {
            connection.setRemoteDescription(description)
            described = true
            pendingCandidates.forEach { connection.addIceCandidate(it) }
            pendingCandidates.clear()
        }

        suspend fun add(candidate: WebRtc.IceCandidate) {
            if (described) connection.addIceCandidate(candidate) else pendingCandidates += candidate
        }

        fun close() {
            watching.cancel()
            connection.close()
        }
    }
}
