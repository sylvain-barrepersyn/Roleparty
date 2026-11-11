package app.roleparty.campaign.agenda.frontend.container

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResponseBody
import app.roleparty.campaign.agenda.contract.web.AgendaResponseBody
import app.roleparty.campaign.agenda.contract.web.ParticipantBody
import app.roleparty.campaign.agenda.contract.web.PresenceBody
import app.roleparty.campaign.agenda.contract.web.PresenceName
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

internal class FakeAgendaApi : AgendaApi {

    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null
    var presenceFailure: BackendFailure? = null
    val scheduledId: GameSessionId = GameSessionId.generate()
    val sessions = mutableListOf(
        GameSessionResponseBody(GameSessionId.generate(), LocalDate(2027, 3, 12), "Into the mists"),
        GameSessionResponseBody(GameSessionId.generate(), LocalDate(2027, 1, 8), null),
    )

    private fun <T> answer(call: String, value: () -> T): Outcome<T, BackendFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value())
    }

    val gameMaster: AccountId = AccountId.generate()
    val player: AccountId = AccountId.generate()
    val participants = listOf(ParticipantBody(gameMaster, true, emptyList()), ParticipantBody(player, false, listOf("Gimli")))

    override suspend fun agenda(campaignId: CampaignId) = answer("sessions") { AgendaResponseBody(player, participants, sessions) }

    override suspend fun scheduleGameSession(campaignId: CampaignId, date: LocalDate) = answer("schedule $date") {
        sessions += GameSessionResponseBody(scheduledId, date, null, presences = listOf(PresenceBody(gameMaster, PresenceName.IN_PERSON_WITH_COMPUTER)))
        scheduledId
    }

    override suspend fun renameGameSession(campaignId: CampaignId, gameSessionId: GameSessionId, name: String) = answer("rename $name") { }

    override suspend fun changePresence(campaignId: CampaignId, gameSessionId: GameSessionId, participant: AccountId, presence: PresenceName) =
        presenceFailure?.let { Outcome.Failure(it) } ?: answer("presence ${if (participant == player) "player" else "game master"} $presence") { }

    override suspend fun changeDigitalGamingTable(campaignId: CampaignId, gameSessionId: GameSessionId, available: Boolean) =
        answer("digital gaming table $available") { }

    var preference = AgendaPreferenceResponseBody(gameMaster = true, presence = PresenceName.IN_PERSON_WITH_COMPUTER, digitalGamingTable = false)
    var preferenceFailure: BackendFailure? = null

    override suspend fun preference(campaignId: CampaignId) = answer("preference") { preference }

    override suspend fun changePreference(campaignId: CampaignId, presence: PresenceName, digitalGamingTable: Boolean) =
        preferenceFailure?.let { Outcome.Failure(it) } ?: answer("change preference $presence $digitalGamingTable") { }
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
        viewModel.loaded().selected?.presences shouldBe listOf(PresenceBody(api.gameMaster, PresenceName.IN_PERSON_WITH_COMPUTER))
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

    test("the participants should be loaded with the agenda, and presences saved at once") {
        val api = FakeAgendaApi()
        val viewModel = AgendaViewModel(api, campaignId)
        viewModel.loaded().requester shouldBe api.player
        viewModel.loaded().participants shouldBe api.participants

        viewModel.handle(AgendaAction.Select(api.sessions.first().id))
        viewModel.handle(AgendaAction.ChangePresence(api.player, PresenceName.REMOTE))
        viewModel.handle(AgendaAction.ChangePresence(api.player, PresenceName.IN_PERSON_WITHOUT_COMPUTER))
        viewModel.handle(AgendaAction.ChangeDigitalGamingTable(true))

        viewModel.loaded().selected?.presences shouldBe listOf(PresenceBody(api.player, PresenceName.IN_PERSON_WITHOUT_COMPUTER))
        viewModel.loaded().selected?.digitalGamingTable shouldBe true
        api.calls shouldContainExactly listOf(
            "sessions",
            "presence player REMOTE",
            "presence player IN_PERSON_WITHOUT_COMPUTER",
            "digital gaming table true",
        )
    }

    test("a refused presence should be shown and the agenda reloaded to its saved state") {
        val api = FakeAgendaApi()
        val viewModel = AgendaViewModel(api, campaignId)
        val sessionId = api.sessions.first().id
        viewModel.handle(AgendaAction.Select(sessionId))

        api.presenceFailure = BackendFailure.Rejected("campaign.agenda.game-session-in-the-past", "The session is over.")
        viewModel.handle(AgendaAction.ChangePresence(api.player, PresenceName.REMOTE))

        viewModel.loaded().selectedId shouldBe sessionId
        viewModel.loaded().failure shouldBe BackendFailure.Rejected("campaign.agenda.game-session-in-the-past", "The session is over.")
        viewModel.loaded().selected?.presences shouldBe emptyList()
    }
})
