package app.roleparty.campaign.agenda.frontend.container

import app.roleparty.campaign.agenda.contract.web.AgendaPreferenceResponseBody
import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.campaign.agenda.frontend.container.settings.AgendaSettingsAction
import app.roleparty.campaign.agenda.frontend.container.settings.AgendaSettingsState
import app.roleparty.campaign.agenda.frontend.container.settings.AgendaSettingsViewModel
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.ktor.client.BackendFailure
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaSettingsViewModelTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()
    val campaignId = CampaignId.generate()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    test("the defaults should be loaded, then each change saved at once") {
        val api = FakeAgendaApi()
        val viewModel = AgendaSettingsViewModel(api, campaignId)

        viewModel.handle(AgendaSettingsAction.ChangePresence(PresenceName.REMOTE))
        viewModel.handle(AgendaSettingsAction.ChangeDigitalGamingTable(true))
        viewModel.handle(AgendaSettingsAction.ChangePresence(PresenceName.IN_PERSON_WITHOUT_COMPUTER))

        viewModel.state.value shouldBe AgendaSettingsState.Loaded(
            AgendaPreferenceResponseBody(gameMaster = true, presence = PresenceName.IN_PERSON_WITHOUT_COMPUTER, digitalGamingTable = true)
        )
        api.calls shouldContainExactly listOf(
            "preference",
            "change preference REMOTE false",
            "change preference REMOTE true",
            "change preference IN_PERSON_WITHOUT_COMPUTER true",
        )
    }

    test("a refused change should be undone and shown") {
        val api = FakeAgendaApi().apply { preferenceFailure = BackendFailure.Unreachable }
        val viewModel = AgendaSettingsViewModel(api, campaignId)

        viewModel.handle(AgendaSettingsAction.ChangePresence(PresenceName.REMOTE))

        viewModel.state.value shouldBe AgendaSettingsState.Loaded(api.preference, BackendFailure.Unreachable)
    }

    test("defaults that can't be loaded should be retried") {
        val api = FakeAgendaApi().apply { failure = BackendFailure.Unreachable }
        val viewModel = AgendaSettingsViewModel(api, campaignId)
        viewModel.state.value shouldBe AgendaSettingsState.Failed(BackendFailure.Unreachable)

        api.failure = null
        viewModel.handle(AgendaSettingsAction.Retry)

        viewModel.state.value shouldBe AgendaSettingsState.Loaded(api.preference)
    }
})
