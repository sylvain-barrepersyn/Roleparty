package app.roleparty.campaign.frontend.container

import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.frontend.component.mastered.MasteredCampaignSection
import app.roleparty.campaign.frontend.container.mastered.MasteredCampaignAction
import app.roleparty.campaign.frontend.container.mastered.MasteredCampaignEvent
import app.roleparty.campaign.frontend.container.mastered.MasteredCampaignState
import app.roleparty.campaign.frontend.container.mastered.MasteredCampaignViewModel
import app.roleparty.campaign.frontend.component.played.PlayedCampaignSection
import app.roleparty.campaign.frontend.container.played.PlayedCampaignAction
import app.roleparty.campaign.frontend.container.played.PlayedCampaignEvent
import app.roleparty.campaign.frontend.container.played.PlayedCampaignState
import app.roleparty.campaign.frontend.container.played.PlayedCampaignViewModel
import app.roleparty.campaign.frontend.container.settings.CampaignSettingsAction
import app.roleparty.campaign.frontend.container.settings.CampaignSettingsState
import app.roleparty.campaign.frontend.container.settings.CampaignSettingsViewModel
import app.roleparty.core.ktor.client.BackendFailure
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MasteredCampaignViewModelsTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    test("the campaign should be loaded on the scenes, then show the chosen section and report the exit") {
        val api = FakeCampaignApi()
        val viewModel = MasteredCampaignViewModel(api, api.campaignId)
        val events = viewModel.recordedEvents()

        viewModel.state.value shouldBe MasteredCampaignState.Loaded(api.campaign, MasteredCampaignSection.SCENES)

        viewModel.handle(MasteredCampaignAction.ShowSection(MasteredCampaignSection.LIBRARY))
        viewModel.handle(MasteredCampaignAction.Exit)

        viewModel.state.value shouldBe MasteredCampaignState.Loaded(api.campaign, MasteredCampaignSection.LIBRARY)
        events shouldContainExactly listOf(MasteredCampaignEvent.Exited)
    }

    test("a campaign that can't be loaded should be retried") {
        val api = FakeCampaignApi().apply { failure = BackendFailure.Unreachable }
        val viewModel = MasteredCampaignViewModel(api, api.campaignId)
        viewModel.state.value shouldBe MasteredCampaignState.Failed(BackendFailure.Unreachable)

        api.failure = null
        viewModel.handle(MasteredCampaignAction.Retry)

        viewModel.state.value shouldBe MasteredCampaignState.Loaded(api.campaign)
    }

    test("a played campaign should be shown as the chosen character on the agenda, and report the exit") {
        val api = FakeCreatureApi()
        val viewModel = PlayedCampaignViewModel(api, api.playerCharacter.id)
        val events = viewModel.recordedEvents()

        viewModel.handle(PlayedCampaignAction.Exit)

        viewModel.state.value shouldBe PlayedCampaignState.Loaded(api.playerCharacter, PlayedCampaignSection.AGENDA)
        api.calls shouldContainExactly listOf("player character ${api.playerCharacter.id}")
        events shouldContainExactly listOf(PlayedCampaignEvent.Exited)
    }

    test("a played campaign whose character can't be loaded should be retried") {
        val api = FakeCreatureApi().apply { failure = BackendFailure.Unreachable }
        val viewModel = PlayedCampaignViewModel(api, api.playerCharacter.id)
        viewModel.state.value shouldBe PlayedCampaignState.Failed(BackendFailure.Unreachable)

        api.failure = null
        viewModel.handle(PlayedCampaignAction.Retry)

        viewModel.state.value shouldBe PlayedCampaignState.Loaded(api.playerCharacter)
    }

    test("a generated invitation code should be added to the existing ones") {
        val api = FakeCampaignApi()
        val viewModel = CampaignSettingsViewModel(api, api.campaignId)

        viewModel.handle(CampaignSettingsAction.GenerateInvitation)

        viewModel.state.value shouldBe CampaignSettingsState.Loaded(
            listOf(InvitationResponseBody("ABC-DEF-GHJ-KLM", used = true), InvitationResponseBody("NPQ-RST-UVW-XYZ", used = false)),
        )
    }
})
