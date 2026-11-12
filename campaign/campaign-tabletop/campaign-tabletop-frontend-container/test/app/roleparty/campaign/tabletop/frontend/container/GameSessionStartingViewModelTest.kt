package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.frontend.container.starting.GameSessionStartingAction
import app.roleparty.campaign.tabletop.frontend.container.starting.GameSessionStartingEvent
import app.roleparty.campaign.tabletop.frontend.container.starting.GameSessionStartingState
import app.roleparty.campaign.tabletop.frontend.container.starting.GameSessionStartingViewModel
import app.roleparty.core.ktor.client.BackendFailure
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class GameSessionStartingViewModelTest : FunSpec({

    val campaignId = CampaignId.generate()

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    test("a game session should be started once") {
        val api = FakeTabletopApi()
        val viewModel = GameSessionStartingViewModel(api, campaignId, api.gameSession.id)
        val events = viewModel.recordedEvents()

        viewModel.handle(GameSessionStartingAction.Refresh)
        viewModel.state.value shouldBe GameSessionStartingState.Ready(started = false)
        viewModel.handle(GameSessionStartingAction.Start)

        viewModel.state.value shouldBe GameSessionStartingState.Ready(started = true)
        api.calls shouldContainExactly listOf("tabletop", "start ${api.gameSession.id}")
        events.shouldBeEmpty()
    }

    test("only the game session the campaign is playing should show as started") {
        val api = FakeTabletopApi().apply { tabletop = TabletopResponseBody(gameSession) }
        val playing = GameSessionStartingViewModel(api, campaignId, api.gameSession.id)
        val other = GameSessionStartingViewModel(api, campaignId, GameSessionId.generate())

        playing.handle(GameSessionStartingAction.Refresh)
        other.handle(GameSessionStartingAction.Refresh)

        playing.state.value shouldBe GameSessionStartingState.Ready(started = true)
        other.state.value shouldBe GameSessionStartingState.Ready(started = false)
    }

    test("a refused start should be reported and leave the game session to start") {
        val refusal = BackendFailure.Rejected("campaign.game-master-required", "Only the game master can do this.")
        val api = FakeTabletopApi().apply { startFailure = refusal }
        val viewModel = GameSessionStartingViewModel(api, campaignId, api.gameSession.id)
        val events = viewModel.recordedEvents()

        viewModel.handle(GameSessionStartingAction.Start)

        viewModel.state.value shouldBe GameSessionStartingState.Ready(started = false)
        events shouldContainExactly listOf(GameSessionStartingEvent.StartFailed(refusal))
    }
})
