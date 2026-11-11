package app.roleparty.campaign.frontend.container

import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.container.creation.CampaignCreationAction
import app.roleparty.campaign.frontend.container.creation.CampaignCreationEvent
import app.roleparty.campaign.frontend.container.creation.CampaignCreationState
import app.roleparty.campaign.frontend.container.creation.CampaignCreationViewModel
import app.roleparty.campaign.frontend.container.joining.CampaignJoiningAction
import app.roleparty.campaign.frontend.container.joining.CampaignJoiningEvent
import app.roleparty.campaign.frontend.container.joining.CampaignJoiningState
import app.roleparty.campaign.frontend.container.joining.CampaignJoiningViewModel
import app.roleparty.campaign.frontend.container.selection.CampaignSelectionAction
import app.roleparty.campaign.frontend.container.selection.CampaignSelectionEvent
import app.roleparty.campaign.frontend.container.selection.CampaignSelectionState
import app.roleparty.campaign.frontend.container.selection.CampaignSelectionViewModel
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.test.ControlledClock
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignOnboardingViewModelsTest : FunSpec({

    val today = LocalDate(2026, 11, 10)
    val clock = ControlledClock(LocalDateTime(2026, 11, 9, 23, 30).toInstant(TimeZone.UTC))

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    test("the selection should load the mastered campaigns, then the played ones on demand, from the local day") {
        val api = FakeCampaignApi()
        val viewModel = CampaignSelectionViewModel(api, clock, TimeZone.of("Europe/Paris"))

        viewModel.handle(CampaignSelectionAction.Refresh)
        viewModel.state.value shouldBe CampaignSelectionState.Loaded(Participation.GAME_MASTER, api.mastered, today)

        viewModel.handle(CampaignSelectionAction.ShowParticipation(Participation.PLAYER))
        viewModel.state.value shouldBe CampaignSelectionState.Loaded(Participation.PLAYER, api.played, today)
        api.calls shouldContainExactly listOf("campaigns GAME_MASTER $today", "campaigns PLAYER $today")
    }

    test("the selection should show a failure and report the requested navigations") {
        val api = FakeCampaignApi().apply { failure = BackendFailure.Unreachable }
        val viewModel = CampaignSelectionViewModel(api, clock)
        val events = viewModel.recordedEvents()

        viewModel.handle(CampaignSelectionAction.Refresh)
        viewModel.handle(CampaignSelectionAction.OpenCampaign(api.campaignId))
        viewModel.handle(CampaignSelectionAction.CreateCampaign)
        viewModel.handle(CampaignSelectionAction.JoinCampaign)

        viewModel.state.value shouldBe CampaignSelectionState.Failed(Participation.GAME_MASTER, BackendFailure.Unreachable)
        events shouldContainExactly listOf(
            CampaignSelectionEvent.MasteredCampaignOpened(api.campaignId),
            CampaignSelectionEvent.CampaignCreationRequested,
            CampaignSelectionEvent.CampaignJoiningRequested,
        )
    }

    test("a campaign opened from the player tab should be opened as played") {
        val api = FakeCampaignApi()
        val viewModel = CampaignSelectionViewModel(api, clock)
        val events = viewModel.recordedEvents()

        viewModel.handle(CampaignSelectionAction.ShowParticipation(Participation.PLAYER))
        viewModel.handle(CampaignSelectionAction.OpenCampaign(api.campaignId))

        events shouldContainExactly listOf(CampaignSelectionEvent.PlayedCampaignOpened(api.campaignId))
    }

    test("a campaign should only be created once named and illustrated") {
        val api = FakeCampaignApi()
        val viewModel = CampaignCreationViewModel(api)
        val events = viewModel.recordedEvents()

        viewModel.handle(CampaignCreationAction.ChangeName("  Curse of Strahd "))
        viewModel.handle(CampaignCreationAction.Submit)
        api.calls.shouldBeEmpty()

        viewModel.handle(CampaignCreationAction.ChangeDescription("Barovia awaits."))
        viewModel.handle(CampaignCreationAction.ChangeImage(ImageId.generate()))
        viewModel.handle(CampaignCreationAction.Submit)

        api.calls shouldContainExactly listOf("create campaign Curse of Strahd|Barovia awaits.")
        events shouldContainExactly listOf(CampaignCreationEvent.CampaignCreated(api.createdCampaignId))
    }

    test("a refused creation should be shown and allow another attempt") {
        val api = FakeCampaignApi().apply { failure = BackendFailure.Rejected("campaign.blank-name", "Name it.") }
        val viewModel = CampaignCreationViewModel(api)
        val imageId = ImageId.generate()

        viewModel.handle(CampaignCreationAction.ChangeName("Strahd"))
        viewModel.handle(CampaignCreationAction.ChangeImage(imageId))
        viewModel.handle(CampaignCreationAction.Submit)

        viewModel.state.value shouldBe CampaignCreationState.Form(
            name = "Strahd",
            imageId = imageId,
            submitting = false,
            failure = BackendFailure.Rejected("campaign.blank-name", "Name it."),
        )
    }

    test("an invitation code should be joined in capitals") {
        val api = FakeCampaignApi()
        val viewModel = CampaignJoiningViewModel(api)
        val events = viewModel.recordedEvents()

        viewModel.handle(CampaignJoiningAction.ChangeCode("abc-def-ghj-klm"))
        viewModel.handle(CampaignJoiningAction.Submit)

        api.calls shouldContainExactly listOf("join ABC-DEF-GHJ-KLM")
        events shouldContainExactly listOf(CampaignJoiningEvent.CampaignJoined(api.campaignId))
    }

    test("a refused invitation code should be shown") {
        val api = FakeCampaignApi().apply { failure = BackendFailure.Rejected("campaign.invalid-invitation-code", "Unknown code.") }
        val viewModel = CampaignJoiningViewModel(api)

        viewModel.handle(CampaignJoiningAction.ChangeCode("ZZZ"))
        viewModel.handle(CampaignJoiningAction.Submit)

        viewModel.state.value shouldBe CampaignJoiningState.Form("ZZZ", failure = BackendFailure.Rejected("campaign.invalid-invitation-code", "Unknown code."))
    }
})
