package app.roleparty.campaign.tabletop.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class AttendanceTest : FunSpec({

    val gameMaster = Seat(AccountId.generate(), Occupant.GameMaster)
    val gimli = Seat(AccountId.generate(), Occupant.Player(PlayerCharacterId.generate(), "Gimli", ImageId.generate()))
    val aragorn = Seat(AccountId.generate(), Occupant.Player(PlayerCharacterId.generate(), "Aragorn", ImageId.generate()))

    test("the game master should attend as soon as seated") {
        Attendance().take(gameMaster).attendees shouldContainExactly listOf(Attendee(gameMaster))
    }

    test("a player should attend only once they hear the game master") {
        val seated = Attendance().take(gameMaster).take(gimli)
        seated.attendees shouldContainExactly listOf(Attendee(gameMaster))
        seated.seated shouldContainExactly listOf(gameMaster, gimli)

        val hearing = seated.hearGameMaster(gimli.accountId, hearing = true)
        hearing.attendees shouldContainExactly listOf(Attendee(gameMaster), Attendee(gimli))

        hearing.hearGameMaster(gimli.accountId, hearing = false).attendees shouldContainExactly listOf(Attendee(gameMaster))
    }

    test("the game master should come first, then the players by character name") {
        val attendance = Attendance().take(gimli).take(aragorn).take(gameMaster)
            .hearGameMaster(gimli.accountId, hearing = true)
            .hearGameMaster(aragorn.accountId, hearing = true)

        attendance.attendees.map { it.seat } shouldContainExactly listOf(gameMaster, aragorn, gimli)
    }

    test("nobody should attend without the game master, nor hear them before they sit") {
        val withoutGameMaster = Attendance().take(gimli).hearGameMaster(gimli.accountId, hearing = true)
        withoutGameMaster.attendees.shouldBeEmpty()
        withoutGameMaster.take(gameMaster).attendees shouldContainExactly listOf(Attendee(gameMaster))
    }

    test("when the game master leaves, the players should stop attending until they hear them again") {
        val attendance = Attendance().take(gameMaster).take(gimli).hearGameMaster(gimli.accountId, hearing = true)

        val left = attendance.leave(gameMaster.accountId)
        left.attendees.shouldBeEmpty()
        left.seated shouldContainExactly listOf(gimli)
        left.take(gameMaster).attendees shouldContainExactly listOf(Attendee(gameMaster))
    }

    test("a player who leaves or sits again should no longer attend") {
        val attendance = Attendance().take(gameMaster).take(gimli).hearGameMaster(gimli.accountId, hearing = true)

        attendance.leave(gimli.accountId).attendees shouldContainExactly listOf(Attendee(gameMaster))
        attendance.take(gimli).attendees shouldContainExactly listOf(Attendee(gameMaster))
        attendance.leave(AccountId.generate()) shouldBe attendance
    }

    test("the camera and the microphone should follow their latest change, and be off for a new seat") {
        val attendance = Attendance().take(gameMaster).changeMedia(gameMaster.accountId, camera = true, microphone = false)
        attendance.attendees shouldContainExactly listOf(Attendee(gameMaster, camera = true, microphone = false))

        attendance.changeMedia(gameMaster.accountId, camera = true, microphone = true).attendees shouldContainExactly
            listOf(Attendee(gameMaster, camera = true, microphone = true))
        attendance.take(gameMaster).attendees shouldContainExactly listOf(Attendee(gameMaster))
        attendance.changeMedia(AccountId.generate(), camera = true, microphone = true) shouldBe attendance
    }
})
