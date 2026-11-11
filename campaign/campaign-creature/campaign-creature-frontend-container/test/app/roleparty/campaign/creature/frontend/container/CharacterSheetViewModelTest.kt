package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.container.character.CharacterSheetAction
import app.roleparty.campaign.creature.frontend.container.character.CharacterSheetEvent
import app.roleparty.campaign.creature.frontend.container.character.CharacterSheetState
import app.roleparty.campaign.creature.frontend.container.character.CharacterSheetViewModel
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
class CharacterSheetViewModelTest : FunSpec({

    val campaignId = CampaignId.generate()

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun CharacterSheetViewModel.loaded() = state.value.shouldBeInstanceOf<CharacterSheetState.Loaded>()

    suspend fun linkedCharacter(api: FakeCreatureApi) =
        api.createCharacterSheet(campaignId, api.sheets.entries.first { it.value.name == "Zombie" }.key).let { api.createdCharacterId }

    test("a linked character should show its monster") {
        val api = FakeCreatureApi()
        val viewModel = CharacterSheetViewModel(api, campaignId, linkedCharacter(api))

        viewModel.loaded().monster shouldBe sheet("Zombie")
    }

    test("a linked character should be saved with its name and notes, but without stats") {
        val api = FakeCreatureApi()
        val id = linkedCharacter(api)
        val viewModel = CharacterSheetViewModel(api, campaignId, id)
        val events = viewModel.recordedEvents()

        viewModel.handle(CharacterSheetAction.StartEditing)
        viewModel.handle(CharacterSheetAction.Change(viewModel.loaded().character.sheet.copy(name = "Ezmerelda")))
        viewModel.handle(CharacterSheetAction.ChangeNotes("Hunts **vampires**."))
        viewModel.handle(CharacterSheetAction.Save)

        api.calls.last() shouldBe "update character Ezmerelda without stats"
        api.characters.getValue(id).notes shouldBe "Hunts **vampires**."
        viewModel.loaded().editing shouldBe false
        events shouldContainExactly listOf(CharacterSheetEvent.Saved)
    }

    test("an unlinked character should be saved with its stats") {
        val api = FakeCreatureApi()
        val id = linkedCharacter(api)
        val viewModel = CharacterSheetViewModel(api, campaignId, id)
        val events = viewModel.recordedEvents()

        viewModel.handle(CharacterSheetAction.Unlink)
        viewModel.loaded().monster shouldBe null
        viewModel.handle(CharacterSheetAction.StartEditing)
        viewModel.handle(CharacterSheetAction.Change(viewModel.loaded().character.sheet.copy(passivePerception = 14)))
        viewModel.handle(CharacterSheetAction.Save)

        api.calls.last() shouldBe "update character Zombie with stats"
        events shouldContainExactly listOf(CharacterSheetEvent.Saved, CharacterSheetEvent.Saved)
    }
})
