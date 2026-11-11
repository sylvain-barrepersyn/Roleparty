package app.roleparty.campaign.story.frontend.container

import app.roleparty.core.document.Document
import app.roleparty.core.document.TextRun
import app.roleparty.core.document.Paragraph
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.contract.web.SceneResponseBody
import app.roleparty.campaign.story.frontend.container.data.StoryApi
import app.roleparty.campaign.story.frontend.container.scene.ScenesAction
import app.roleparty.campaign.story.frontend.container.scene.ScenesState
import app.roleparty.campaign.story.frontend.container.scene.ScenesViewModel
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId
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

private class FakeStoryApi : StoryApi {

    val campaignId: CampaignId = CampaignId.generate()
    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null
    val scenes = mutableListOf(SceneResponseBody(SceneId.generate(), "Arrival", document("Mist everywhere."), emptyList()))
    val createdSceneId: SceneId = SceneId.generate()
    val libraryEntries = listOf(entry("Village"), entry("Castle"))

    private fun <T> answer(call: String, value: () -> T): Outcome<T, BackendFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value())
    }

    override suspend fun scenes(campaignId: CampaignId) = answer("scenes") { scenes.toList() }

    override suspend fun createScene(campaignId: CampaignId, name: String, description: Document) = answer("create scene $name") { createdSceneId }

    override suspend fun changeScene(campaignId: CampaignId, sceneId: SceneId, name: String, description: Document) =
        answer("change scene $name|${description.paragraphs.joinToString { it.text }}") { }

    override suspend fun deleteScene(campaignId: CampaignId, sceneId: SceneId) = answer("delete scene") { }

    override suspend fun libraryEntries(campaignId: CampaignId) = answer("library") { libraryEntries }

    override suspend fun linkLibraryEntry(campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId) = answer("link") { }

    override suspend fun unlinkLibraryEntry(campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId) = answer("unlink") { }

    private fun entry(title: String) = LibraryEntryResponseBody(LibraryEntryId.generate(), title, "", ImageId.generate())
}

@OptIn(ExperimentalCoroutinesApi::class)
class ScenesViewModelTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun ScenesViewModel.loaded() = state.value.shouldBeInstanceOf<ScenesState.Loaded>()

    test("library entries should be loaded with the scenes, sorted by title") {
        val viewModel = ScenesViewModel(FakeStoryApi(), CampaignId.generate())

        viewModel.loaded().libraryEntries.map { it.title } shouldContainExactly listOf("Castle", "Village")
    }

    test("a created scene should be selected, edited, linked to a library entry and deleted") {
        val api = FakeStoryApi()
        val viewModel = ScenesViewModel(api, api.campaignId)
        val entry = api.libraryEntries.first()

        viewModel.handle(ScenesAction.Create("New scene"))
        viewModel.loaded().selected?.id shouldBe api.createdSceneId

        viewModel.handle(ScenesAction.ChangeDescription(document("Fog rolls in")))
        viewModel.handle(ScenesAction.LinkLibraryEntry(entry.id))
        viewModel.loaded().selected?.libraryEntryIds shouldContainExactly listOf(entry.id)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.handle(ScenesAction.UnlinkLibraryEntry(entry.id))
        viewModel.loaded().selected?.libraryEntryIds shouldContainExactly emptyList()
        viewModel.handle(ScenesAction.Delete)

        viewModel.loaded().scenes.map { it.name } shouldContainExactly listOf("Arrival")
        viewModel.loaded().selected.shouldBeNull()
        api.calls.filterNot { it == "library" } shouldContainExactly listOf(
            "scenes", "create scene New scene", "link", "change scene New scene|Fog rolls in", "unlink", "delete scene",
        )
    }

    test("a refused scene change should be shown") {
        val api = FakeStoryApi()
        val viewModel = ScenesViewModel(api, api.campaignId)
        viewModel.handle(ScenesAction.Select(api.scenes.first().id))

        api.failure = BackendFailure.Rejected("campaign.blank-name", "Name it.")
        viewModel.handle(ScenesAction.ChangeName(" "))
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.loaded().failure shouldBe BackendFailure.Rejected("campaign.blank-name", "Name it.")
    }
})

private fun document(text: String) = Document.from(listOf(Paragraph(runs = listOf(TextRun(text)))))
