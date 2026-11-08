package app.roleparty.campaign.library.frontend.container

import app.roleparty.campaign.library.frontend.container.library.LibraryAction
import app.roleparty.campaign.library.frontend.container.library.LibraryState
import app.roleparty.campaign.library.frontend.container.library.LibraryViewModel
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelsTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun LibraryViewModel.loaded() = state.value.shouldBeInstanceOf<LibraryState.Loaded>()

    test("entries should be sorted by title, and their edits saved once typing pauses") {
        val api = FakeLibraryApi()
        val viewModel = LibraryViewModel(api, api.campaignId)
        viewModel.loaded().entries.map { it.title } shouldContainExactly listOf("Castle", "Village")

        viewModel.handle(LibraryAction.Select(api.entries.first().id))
        viewModel.handle(LibraryAction.ChangeTitle("Vallaki"))
        viewModel.handle(LibraryAction.ChangeDescription("A walled town"))
        viewModel.loaded().selected?.title shouldBe "Vallaki"
        api.calls shouldContainExactly listOf("entries")

        dispatcher.scheduler.advanceUntilIdle()

        api.calls shouldContainExactly listOf("entries", "change Vallaki|A walled town")
    }

    test("an entry should be created with its image, then selected") {
        val api = FakeLibraryApi()
        val viewModel = LibraryViewModel(api, api.campaignId)

        viewModel.handle(LibraryAction.OpenCreation)
        viewModel.handle(LibraryAction.ChangeCreationTitle("Amber temple"))
        viewModel.handle(LibraryAction.SubmitCreation)
        viewModel.loaded().creation.shouldNotBeNull().submitting shouldBe false

        viewModel.handle(LibraryAction.ChangeCreationImage(ImageId.generate()))
        viewModel.handle(LibraryAction.SubmitCreation)

        viewModel.loaded().creation.shouldBeNull()
        viewModel.loaded().selected?.title shouldBe "Amber temple"
        api.calls shouldContainExactly listOf("entries", "create Amber temple|", "entries")
    }
})
