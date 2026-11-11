package app.roleparty.campaign.library.frontend.container

import app.roleparty.campaign.library.contract.web.JournalEntryResponseBody
import app.roleparty.campaign.library.frontend.container.journal.JournalAction
import app.roleparty.campaign.library.frontend.container.journal.JournalState
import app.roleparty.campaign.library.frontend.container.journal.JournalViewModel
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun JournalViewModel.loaded() = state.value.shouldBeInstanceOf<JournalState.Loaded>()

    test("the journal should open on its newest entry, and a new entry should be added on top and selected") {
        val api = FakeLibraryApi()
        val viewModel = JournalViewModel(api, api.playerCharacterId)
        viewModel.loaded().selected shouldBe api.journal.first()

        viewModel.handle(JournalAction.CreateEntry)

        viewModel.loaded().entries.first() shouldBe JournalEntryResponseBody(api.createdJournalEntryId, "", "", emptyList())
        viewModel.loaded().selectedId shouldBe api.createdJournalEntryId
        api.calls shouldContainExactly listOf("journal", "create journal entry")
    }

    test("the text should be saved once typing pauses, and images saved as soon as they change") {
        val api = FakeLibraryApi()
        val viewModel = JournalViewModel(api, api.playerCharacterId)
        val image = ImageId.generate()

        viewModel.handle(JournalAction.ChangeTitle("Day 2 "))
        viewModel.handle(JournalAction.ChangeText("# Vallaki"))
        api.calls shouldContainExactly listOf("journal")

        dispatcher.scheduler.advanceUntilIdle()
        api.calls shouldContainExactly listOf("journal", "update journal entry Day 2|# Vallaki|0 images")

        viewModel.handle(JournalAction.AddImage(image))
        viewModel.handle(JournalAction.AddImage(image))
        viewModel.loaded().selected?.images shouldBe listOf(image)
        viewModel.handle(JournalAction.RemoveImage(image))

        viewModel.loaded().selected?.images shouldBe emptyList()
        api.calls.drop(2) shouldContainExactly listOf(
            "update journal entry Day 2|# Vallaki|1 images",
            "update journal entry Day 2|# Vallaki|1 images",
            "update journal entry Day 2|# Vallaki|0 images",
        )
    }

    test("a journal that can't be loaded should be retried, and a failed creation shown") {
        val api = FakeLibraryApi().apply { failure = BackendFailure.Unreachable }
        val viewModel = JournalViewModel(api, api.playerCharacterId)
        viewModel.state.value shouldBe JournalState.Failed(BackendFailure.Unreachable)

        api.failure = null
        viewModel.handle(JournalAction.Retry)
        viewModel.loaded().entries shouldBe api.journal

        api.failure = BackendFailure.Unreachable
        viewModel.handle(JournalAction.CreateEntry)
        viewModel.loaded().failure shouldBe BackendFailure.Unreachable
        viewModel.loaded().creating shouldBe false
    }
})
