package app.roleparty.campaign.tabletop.domain.model

import app.roleparty.account.commons.AccountId

data class Attendee(val seat: Seat, val camera: Boolean = false, val microphone: Boolean = false)

@ConsistentCopyVisibility
data class Attendance private constructor(
    private val seats: Map<AccountId, Attendee>,
    private val hearingGameMaster: Set<AccountId>,
) {

    constructor() : this(emptyMap(), emptySet())

    val seated: List<Seat> get() = seats.values.map { it.seat }

    val attendees: List<Attendee>
        get() {
            val gameMaster = seats.values.firstOrNull { it.seat.gameMaster } ?: return emptyList()
            val players = seats.values
                .filter { !it.seat.gameMaster && it.seat.accountId in hearingGameMaster }
                .sortedWith(compareBy({ (it.seat.occupant as? Occupant.Player)?.characterName?.lowercase() }, { it.seat.accountId.toString() }))
            return listOf(gameMaster) + players
        }

    fun take(seat: Seat): Attendance = leave(seat.accountId).run { copy(seats = seats + (seat.accountId to Attendee(seat))) }

    fun leave(accountId: AccountId): Attendance {
        val left = seats[accountId] ?: return this
        return if (left.seat.gameMaster) Attendance(seats - accountId, emptySet()) else copy(seats = seats - accountId, hearingGameMaster = hearingGameMaster - accountId)
    }

    fun hearGameMaster(accountId: AccountId, hearing: Boolean): Attendance {
        val seat = seats[accountId]?.seat ?: return this
        if (seat.gameMaster || seats.values.none { it.seat.gameMaster }) return this
        return copy(hearingGameMaster = if (hearing) hearingGameMaster + accountId else hearingGameMaster - accountId)
    }

    fun changeMedia(accountId: AccountId, camera: Boolean, microphone: Boolean): Attendance {
        val attendee = seats[accountId] ?: return this
        return copy(seats = seats + (accountId to attendee.copy(camera = camera, microphone = microphone)))
    }
}
