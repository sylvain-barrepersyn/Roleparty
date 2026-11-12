package app.roleparty.campaign.tabletop.frontend.container.data

import androidx.compose.ui.graphics.ImageBitmap
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.contract.web.SeatCommandBody
import app.roleparty.campaign.tabletop.contract.web.SeatEventBody
import app.roleparty.campaign.tabletop.contract.web.SeatResponseBody
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import io.ktor.client.webrtc.WebRtc
import io.ktor.client.webrtc.media.getNative
import io.ktor.utils.io.ExperimentalKtorApi
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class WebRtcTabletopCalls(private val api: TabletopApi) : TabletopCalls {

    override fun join(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?): TabletopCall =
        WebRtcTabletopCall(api, campaignId, playerCharacterId)
}

@OptIn(ExperimentalKtorApi::class)
internal class WebRtcTabletopCall(
    private val api: TabletopApi,
    private val campaignId: CampaignId,
    private val playerCharacterId: PlayerCharacterId?,
    private val createMedia: () -> TabletopMedia = ::TabletopMedia,
    private val display: CoroutineDispatcher = Dispatchers.Main,
    private val retryDelay: Duration = 3.seconds,
) : TabletopCall {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()
    private val left = AtomicBoolean(false)

    private val mutableStatus = MutableStateFlow<TableStatus>(TableStatus.Joining)
    override val status: StateFlow<TableStatus> = mutableStatus.asStateFlow()

    private val feeds = ConcurrentHashMap<AccountId, VideoFeed>()
    private val preview = VideoFeed(scope, display)

    @Volatile
    private var self: AccountId? = null
    private var media: TabletopMedia? = null
    private var mesh: PeerMesh? = null
    private var signals: SeatSignals? = null

    private val sitting = scope.launch { sit() }

    override fun video(accountId: AccountId): StateFlow<ImageBitmap?> = if (accountId == self) preview.frame else feedOf(accountId).frame

    override suspend fun turnCamera(on: Boolean): Boolean = withMedia { media ->
        if (on == (media.camera != null)) return@withMedia true
        if (on) {
            val camera = device { media.openCamera() } ?: return@withMedia false
            preview.show(camera.getNative())
            mesh?.refreshTracks()
        } else {
            val camera = media.releaseCamera()
            preview.release()
            mesh?.refreshTracks()
            camera?.close()
        }
        true
    }

    override suspend fun turnMicrophone(on: Boolean): Boolean = withMedia { media ->
        if (on == (media.microphone != null)) return@withMedia true
        if (on) {
            device { media.openMicrophone() } ?: return@withMedia false
            mesh?.refreshTracks()
        } else {
            val microphone = media.releaseMicrophone()
            mesh?.refreshTracks()
            microphone?.close()
        }
        true
    }

    override fun leave() {
        CoroutineScope(Dispatchers.IO).launch { close() }
    }

    suspend fun close() {
        if (!left.compareAndSet(false, true)) return
        sitting.cancelAndJoin()
        mutex.withLock {
            preview.release()
            feeds.values.forEach { it.release() }
            media?.close()
            media = null
        }
        scope.cancel()
    }

    private suspend fun sit() {
        while (true) {
            when (val seat = api.seat(campaignId, playerCharacterId)) {
                is Outcome.Failure -> if (seat.error is BackendFailure.Rejected) return refuse(seat.error)
                is Outcome.Success -> {
                    val media = mutex.withLock { media ?: device { createMedia() }?.also { media = it } } ?: return refuse(BackendFailure.Unexpected)
                    self = seat.value.accountId
                    api.signal(campaignId, playerCharacterId) { exchange(seat.value, media, it) }
                }
            }
            mutableStatus.value = TableStatus.Joining
            delay(retryDelay)
        }
    }

    private fun refuse(failure: BackendFailure) {
        mutableStatus.value = TableStatus.Refused(failure)
    }

    private suspend fun exchange(seat: SeatResponseBody, media: TabletopMedia, signals: SeatSignals) = coroutineScope {
        val iceServers = seat.iceServers.map { WebRtc.IceServer(it.urls, it.username, it.credential) }
        val mesh = PeerMesh(media, iceServers, this, ::feedOf) { to, signal -> signals.attempt(SeatCommandBody.Signal(to, signal)) }
        val gameMasters = MutableStateFlow(GameMasters())
        mutex.withLock {
            this@WebRtcTabletopCall.mesh = mesh
            this@WebRtcTabletopCall.signals = signals
        }
        if (!seat.gameMaster) {
            launch {
                combine(mesh.connected, gameMasters) { connected, masters -> masters.arrivals to masters.accounts.any { it in connected } }
                    .distinctUntilChanged()
                    .collect { signals.attempt(SeatCommandBody.HearGameMaster(hearing = it.second)) }
            }
        }
        try {
            signals.events.collect { event -> mutex.withLock { handle(event, seat, media, mesh, gameMasters, signals) } }
        } finally {
            withContext(NonCancellable) {
                mutex.withLock {
                    mesh.hangUpAll()
                    this@WebRtcTabletopCall.mesh = null
                    this@WebRtcTabletopCall.signals = null
                }
            }
            coroutineContext.cancelChildren()
        }
    }

    private suspend fun handle(
        event: SeatEventBody,
        seat: SeatResponseBody,
        media: TabletopMedia,
        mesh: PeerMesh,
        gameMasters: MutableStateFlow<GameMasters>,
        signals: SeatSignals,
    ) {
        when (event) {
            is SeatEventBody.Welcomed -> {
                gameMasters.value = GameMasters(accounts = event.peers.filter { it.gameMaster }.map { it.accountId }.toSet())
                if (media.camera != null || media.microphone != null) signals.attempt(media.state())
                event.peers.forEach { peer -> reaching(mesh, peer.accountId) { mesh.call(peer.accountId) } }
            }

            is SeatEventBody.PeerJoined -> if (event.peer.gameMaster) gameMasters.update { GameMasters(it.arrivals + 1, it.accounts + event.peer.accountId) }

            is SeatEventBody.PeerLeft -> {
                gameMasters.update { it.copy(accounts = it.accounts - event.accountId) }
                mesh.hangUp(event.accountId)
            }

            is SeatEventBody.Signal -> reaching(mesh, event.from) { mesh.receive(event.from, event.signal) }

            is SeatEventBody.AttendeesChanged -> {
                event.attendees.filter { !it.camera && it.accountId != seat.accountId }.forEach { feedOf(it.accountId).clear() }
                mutableStatus.value = TableStatus.Joined(seat.accountId, event.attendees)
            }
        }
    }

    private suspend fun withMedia(change: suspend (TabletopMedia) -> Boolean): Boolean = withContext(Dispatchers.IO) {
        mutex.withLock {
            val media = media ?: return@withLock false
            change(media).also { changed -> if (changed) signals?.attempt(media.state()) }
        }
    }

    private fun feedOf(accountId: AccountId): VideoFeed = feeds.computeIfAbsent(accountId) { VideoFeed(scope, display) }

    private fun TabletopMedia.state() = SeatCommandBody.ChangeMedia(camera = camera != null, microphone = microphone != null)

    private suspend fun SeatSignals.attempt(command: SeatCommandBody) {
        try {
            send(command)
        } catch (cause: CancellationException) {
            throw cause
        } catch (_: Exception) {
            return
        }
    }

    private suspend fun reaching(mesh: PeerMesh, peer: AccountId, signal: suspend () -> Unit) {
        try {
            signal()
        } catch (cause: CancellationException) {
            throw cause
        } catch (_: Exception) {
            mesh.hangUp(peer)
        }
    }

    private data class GameMasters(val arrivals: Int = 0, val accounts: Set<AccountId> = emptySet())

    private suspend fun <T> device(open: suspend () -> T): T? =
        try {
            open()
        } catch (cause: CancellationException) {
            throw cause
        } catch (_: Exception) {
            null
        } catch (_: LinkageError) {
            null
        }
}
