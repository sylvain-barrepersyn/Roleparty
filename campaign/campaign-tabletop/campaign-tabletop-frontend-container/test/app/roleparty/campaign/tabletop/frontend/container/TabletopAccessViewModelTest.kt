package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.contract.web.TabletopEventBody
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.frontend.container.access.TabletopAccessAction
import app.roleparty.campaign.tabletop.frontend.container.access.TabletopAccessEvent
import app.roleparty.campaign.tabletop.frontend.container.access.TabletopAccessState
import app.roleparty.campaign.tabletop.frontend.container.access.TabletopAccessViewModel
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TabletopAccessViewModelTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()
    val campaignId = CampaignId.generate()
    val gameMaster = AttendeeBody(AccountId.generate(), gameMaster = true)
    val gimli = AttendeeBody(AccountId.generate(), gameMaster = false, characterName = "Gimli", imageId = ImageId.generate())

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    test("a campaign without a started game session should offer no tabletop") {
        val viewModel = TabletopAccessViewModel(FakeTabletopApi(), campaignId)

        viewModel.handle(TabletopAccessAction.Open)

        viewModel.state.value shouldBe TabletopAccessState.Loaded(gameSession = null)
    }

    test("a game session started meanwhile should be announced and open the tabletop to everyone") {
        val api = FakeTabletopApi()
        val viewModel = TabletopAccessViewModel(api, campaignId)
        val events = viewModel.recordedEvents()

        api.push(TabletopEventBody.Snapshot(TabletopResponseBody()))
        api.push(TabletopEventBody.GameSessionStarted(api.gameSession))
        viewModel.handle(TabletopAccessAction.Open)

        events shouldContainExactly listOf(TabletopAccessEvent.GameSessionStarted(api.gameSession))
        viewModel.state.value shouldBe TabletopAccessState.Loaded(api.gameSession, opened = true)

        viewModel.handle(TabletopAccessAction.Close)
        viewModel.state.value shouldBe TabletopAccessState.Loaded(api.gameSession, opened = false)
    }

    test("the people attending should follow who joins and leaves, without announcing the game session again") {
        val api = FakeTabletopApi().apply { tabletop = TabletopResponseBody(gameSession, listOf(gameMaster)) }
        val viewModel = TabletopAccessViewModel(api, campaignId)
        val events = viewModel.recordedEvents()
        viewModel.state.value shouldBe TabletopAccessState.Loaded(api.gameSession, listOf(gameMaster))

        api.push(TabletopEventBody.AttendeesChanged(listOf(gameMaster, gimli)))
        viewModel.state.value shouldBe TabletopAccessState.Loaded(api.gameSession, listOf(gameMaster, gimli))

        api.push(TabletopEventBody.AttendeesChanged(emptyList()))
        viewModel.state.value shouldBe TabletopAccessState.Loaded(api.gameSession, emptyList())
        events.shouldBeEmpty()
    }

    test("a lost connection should be watched again, keeping the tabletop opened") {
        val api = FakeTabletopApi().apply { tabletop = TabletopResponseBody(gameSession, listOf(gameMaster)) }
        val viewModel = TabletopAccessViewModel(api, campaignId)
        viewModel.handle(TabletopAccessAction.Open)

        api.tabletop = TabletopResponseBody(api.gameSession, listOf(gameMaster, gimli))
        api.failure = BackendFailure.Unreachable
        api.disconnect()
        dispatcher.scheduler.advanceTimeBy(4.seconds)
        viewModel.state.value shouldBe TabletopAccessState.Loaded(api.gameSession, listOf(gameMaster), opened = true)

        api.failure = null
        dispatcher.scheduler.advanceTimeBy(4.seconds)
        viewModel.state.value shouldBe TabletopAccessState.Loaded(api.gameSession, listOf(gameMaster, gimli), opened = true)
        api.calls shouldContainExactly listOf("tabletop", "events", "tabletop", "tabletop", "events")
    }

    test("a refused participant should stop watching") {
        val api = FakeTabletopApi().apply { failure = BackendFailure.Rejected("campaign.participant-required", "Participants only.") }
        val viewModel = TabletopAccessViewModel(api, campaignId)

        dispatcher.scheduler.advanceTimeBy(10.seconds)

        viewModel.state.value shouldBe TabletopAccessState.Loading
        api.calls shouldContainExactly listOf("tabletop")
    }
})
