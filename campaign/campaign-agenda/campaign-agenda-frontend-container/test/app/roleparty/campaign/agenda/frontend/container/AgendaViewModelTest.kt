package app.roleparty.campaign.agenda.frontend.container

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.frontend.container.agenda.AgendaAction
import app.roleparty.campaign.agenda.frontend.container.agenda.AgendaState
import app.roleparty.campaign.agenda.frontend.container.agenda.AgendaViewModel
import app.roleparty.campaign.agenda.frontend.container.data.AgendaApi
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate

private class FakeAgendaApi : AgendaApi {

    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null
    val scheduledId: GameSessionId = GameSessionId.generate()
    val sessions = listOf(
        GameSessionResponseBody(GameSessionId.generate(), LocalDate(2027, 3, 12), "Into the mists"),
        GameSessionResponseBody(GameSessionId.generate(), LocalDate(2027, 1, 8), null),
    )

    private fun <T> answer(call: String, value: () -> T): Outcome<T, BackendFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value())
    }

    override suspend fun gameSessions(campaignId: CampaignId) = answer("sessions") { sessions }

    override suspend fun scheduleGameSession(campaignId: CampaignId, date: LocalDate) = answer("schedule $date") { scheduledId }

    override suspend fun renameGameSession(campaignId: CampaignId, gameSessionId: GameSessionId, name: String) = answer("rename $name") { }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModelTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()
    val campaignId = CampaignId.generate()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun AgendaViewModel.loaded() = state.value.shouldBeInstanceOf<AgendaState.Loaded>()

    test("game sessions should be listed by date") {
        val viewModel = AgendaViewModel(FakeAgendaApi(), campaignId)

        viewModel.loaded().gameSessions.map { it.date } shouldContainExactly listOf(LocalDate(2027, 1, 8), LocalDate(2027, 3, 12))
    }

    test("a scheduled game session should take its place by date and be selected") {
        val api = FakeAgendaApi()
        val viewModel = AgendaViewModel(api, campaignId)

        viewModel.handle(AgendaAction.OpenScheduling)
        viewModel.loaded().scheduling shouldBe true
        viewModel.handle(AgendaAction.Schedule(LocalDate(2027, 2, 1)))

        viewModel.loaded().scheduling shouldBe false
        viewModel.loaded().gameSessions.map { it.date } shouldContainExactly listOf(LocalDate(2027, 1, 8), LocalDate(2027, 2, 1), LocalDate(2027, 3, 12))
        viewModel.loaded().selectedId shouldBe api.scheduledId
    }

    test("a game session should be renamed once typing pauses") {
        val api = FakeAgendaApi()
        val viewModel = AgendaViewModel(api, campaignId)

        viewModel.handle(AgendaAction.Select(api.sessions.last().id))
        viewModel.handle(AgendaAction.ChangeName("Arrival"))
        viewModel.handle(AgendaAction.ChangeName("Arrival in Barovia "))
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.loaded().selected?.name shouldBe "Arrival in Barovia "
        api.calls shouldContainExactly listOf("sessions", "rename Arrival in Barovia")
    }

    test("a date refused by the backend should be shown") {
        val api = FakeAgendaApi()
        val viewModel = AgendaViewModel(api, campaignId)

        api.failure = BackendFailure.Rejected("agenda.date-in-the-past", "This date is past.")
        viewModel.handle(AgendaAction.Schedule(LocalDate(2020, 1, 1)))

        viewModel.loaded().failure shouldBe BackendFailure.Rejected("agenda.date-in-the-past", "This date is past.")
    }
})
