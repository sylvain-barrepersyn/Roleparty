package app.roleparty.campaign.tabletop.infrastructure.adapter.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.contract.web.PeerBody
import app.roleparty.campaign.tabletop.contract.web.SeatCommandBody
import app.roleparty.campaign.tabletop.contract.web.SeatEventBody
import app.roleparty.campaign.tabletop.domain.model.Attendance
import app.roleparty.campaign.tabletop.domain.model.Attendee
import app.roleparty.campaign.tabletop.domain.model.Seat
import app.roleparty.campaign.tabletop.domain.model.StartedGameSession
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class TabletopHub {

    private val rooms = ConcurrentHashMap<CampaignId, TabletopRoom>()

    fun room(campaignId: CampaignId): TabletopRoom = rooms.computeIfAbsent(campaignId) { TabletopRoom() }
}

internal class SeatLink(val seat: Seat) {

    val events = Channel<SeatEventBody>(Channel.UNLIMITED)

    val peer get() = PeerBody(seat.accountId, seat.gameMaster)
}

internal class TabletopRoom {

    private val mutex = Mutex()
    private val links = mutableMapOf<AccountId, SeatLink>()
    private val attendance = MutableStateFlow(Attendance())
    private val mutableStarts = MutableSharedFlow<StartedGameSession>(extraBufferCapacity = STARTS_BUFFER)

    val starts: SharedFlow<StartedGameSession> = mutableStarts.asSharedFlow()

    val attendees: Flow<List<Attendee>> = attendance.map { it.attendees }.distinctUntilChanged()

    val currentAttendees: List<Attendee> get() = attendance.value.attendees

    fun announce(started: StartedGameSession) {
        mutableStarts.tryEmit(started)
    }

    suspend fun sit(seat: Seat): SeatLink = mutex.withLock {
        links.remove(seat.accountId)?.let { replaced ->
            replaced.events.close()
            links.values.forEach { it.events.trySend(SeatEventBody.PeerLeft(seat.accountId)) }
        }
        val link = SeatLink(seat)
        link.events.trySend(SeatEventBody.Welcomed(links.values.map { it.peer }))
        links.values.forEach { it.events.trySend(SeatEventBody.PeerJoined(link.peer)) }
        links[seat.accountId] = link
        attendance.update { it.take(seat) }
        link
    }

    suspend fun leave(link: SeatLink) = mutex.withLock {
        val accountId = link.seat.accountId
        if (links[accountId] !== link) return@withLock
        links.remove(accountId)
        link.events.close()
        links.values.forEach { it.events.trySend(SeatEventBody.PeerLeft(accountId)) }
        attendance.update { it.leave(accountId) }
    }

    suspend fun handle(link: SeatLink, command: SeatCommandBody) = mutex.withLock {
        val accountId = link.seat.accountId
        if (links[accountId] !== link) return@withLock
        when (command) {
            is SeatCommandBody.Signal -> links[command.to]?.events?.trySend(SeatEventBody.Signal(accountId, command.signal))
            is SeatCommandBody.HearGameMaster -> attendance.update { it.hearGameMaster(accountId, command.hearing) }
            is SeatCommandBody.ChangeMedia -> attendance.update { it.changeMedia(accountId, command.camera, command.microphone) }
        }
    }

    private companion object {
        const val STARTS_BUFFER = 16
    }
}
