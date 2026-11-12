package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.frontend.container.data.TableStatus
import app.roleparty.campaign.tabletop.frontend.container.tabletop.TabletopAction
import app.roleparty.campaign.tabletop.frontend.container.tabletop.TabletopEvent
import app.roleparty.campaign.tabletop.frontend.container.tabletop.TabletopState
import app.roleparty.campaign.tabletop.frontend.container.tabletop.TabletopViewModel
import app.roleparty.core.ktor.client.BackendFailure
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TabletopViewModelTest : FunSpec({

    val campaignId = CampaignId.generate()
    val self = AccountId.generate()
    val gameMaster = AttendeeBody(AccountId.generate(), gameMaster = true)
    val gimli = AttendeeBody(self, gameMaster = false, characterName = "Gimli")

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun TabletopViewModel.joined() = state.value.shouldBeInstanceOf<TabletopState.Joined>()

    test("a player should join the table with their character, and see who attends") {
        val calls = FakeTabletopCalls()
        val playerCharacterId = PlayerCharacterId.generate()
        val viewModel = TabletopViewModel(calls, campaignId, TabletopParticipation.Player(playerCharacterId))
        calls.joined.shouldBeEmpty()

        viewModel.handle(TabletopAction.Join)
        viewModel.handle(TabletopAction.Join)
        viewModel.state.value shouldBe TabletopState.Joining
        calls.joined.single().playerCharacterId shouldBe playerCharacterId

        calls.joined.single().status.value = TableStatus.Joined(self, listOf(gameMaster, gimli))
        viewModel.joined().self shouldBe self
        viewModel.joined().attendees shouldBe listOf(gameMaster, gimli)
        viewModel.joined().camera shouldBe false
        viewModel.joined().microphone shouldBe false
    }

    test("the camera and the microphone should turn on and off, or report that they can't be used") {
        val calls = FakeTabletopCalls()
        val viewModel = TabletopViewModel(calls, campaignId, TabletopParticipation.GameMaster)
        viewModel.handle(TabletopAction.Join)
        val call = calls.joined.single()
        call.status.value = TableStatus.Joined(self, listOf(gameMaster))

        viewModel.handle(TabletopAction.TurnCamera(on = true))
        viewModel.joined().camera shouldBe true
        viewModel.joined().cameraUnavailable shouldBe false

        call.microphoneAvailable = false
        viewModel.handle(TabletopAction.TurnMicrophone(on = true))
        viewModel.joined().microphone shouldBe false
        viewModel.joined().microphoneUnavailable shouldBe true

        viewModel.handle(TabletopAction.TurnCamera(on = false))
        viewModel.joined().camera shouldBe false
        call.turned shouldContainExactly listOf("camera true", "microphone true", "camera false")
    }

    test("a table joined again after a lost connection should keep the camera on") {
        val calls = FakeTabletopCalls()
        val viewModel = TabletopViewModel(calls, campaignId, TabletopParticipation.GameMaster)
        viewModel.handle(TabletopAction.Join)
        val call = calls.joined.single()
        call.status.value = TableStatus.Joined(self, listOf(gameMaster))
        viewModel.handle(TabletopAction.TurnCamera(on = true))

        call.status.value = TableStatus.Joining
        viewModel.state.value shouldBe TabletopState.Joining
        call.status.value = TableStatus.Joined(self, listOf(gameMaster))

        viewModel.joined().camera shouldBe true
        calls.joined shouldHaveSize 1
    }

    test("closing the window should leave the table, and joining again should take a new seat") {
        val calls = FakeTabletopCalls()
        val viewModel = TabletopViewModel(calls, campaignId, TabletopParticipation.GameMaster)
        val events = viewModel.recordedEvents()
        viewModel.handle(TabletopAction.Join)
        calls.joined.single().status.value = TableStatus.Joined(self, listOf(gameMaster))
        viewModel.handle(TabletopAction.TurnCamera(on = true))

        viewModel.handle(TabletopAction.Close)
        viewModel.handle(TabletopAction.Leave)

        calls.joined.single().left shouldBe true
        viewModel.state.value shouldBe TabletopState.Joining
        events shouldContainExactly listOf(TabletopEvent.Closed)

        viewModel.handle(TabletopAction.Join)
        calls.joined shouldHaveSize 2
        calls.joined.last().status.value = TableStatus.Joined(self, listOf(gameMaster))
        viewModel.joined().camera shouldBe false
    }

    test("a refused seat should be shown and retried") {
        val calls = FakeTabletopCalls()
        val refusal = BackendFailure.Rejected("campaign.tabletop.game-session-not-started", "No game session has been started.")
        val viewModel = TabletopViewModel(calls, campaignId, TabletopParticipation.GameMaster)
        viewModel.handle(TabletopAction.Join)

        calls.joined.single().status.value = TableStatus.Refused(refusal)
        viewModel.state.value shouldBe TabletopState.Failed(refusal)

        viewModel.handle(TabletopAction.Retry)
        calls.joined.first().left shouldBe true
        calls.joined shouldHaveSize 2
        viewModel.state.value shouldBe TabletopState.Joining
    }
})
