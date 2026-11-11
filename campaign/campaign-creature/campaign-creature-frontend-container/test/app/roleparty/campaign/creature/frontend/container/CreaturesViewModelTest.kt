package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.component.CreatureSelection
import app.roleparty.campaign.creature.frontend.container.creatures.CreaturesAction
import app.roleparty.campaign.creature.frontend.container.creatures.CreaturesState
import app.roleparty.campaign.creature.frontend.container.creatures.CreaturesViewModel
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
class CreaturesViewModelTest : FunSpec({

    val campaignId = CampaignId.generate()

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun CreaturesViewModel.loaded() = state.value.shouldBeInstanceOf<CreaturesState.Loaded>()

    test("monsters and characters should both be loaded") {
        val api = FakeCreatureApi()
        val zombie = api.sheets.entries.first { it.value.name == "Zombie" }.key
        api.createCharacterSheet(campaignId, zombie)

        val viewModel = CreaturesViewModel(api, campaignId)

        viewModel.loaded().monsters.map { it.name } shouldContainExactly listOf("Zombie", "Ghoul")
        viewModel.loaded().characters.map { it.monsterSheetId } shouldContainExactly listOf(zombie)
    }

    test("a created monster should be listed and selected") {
        val api = FakeCreatureApi()
        val viewModel = CreaturesViewModel(api, campaignId)

        viewModel.handle(CreaturesAction.CreateMonster)

        viewModel.loaded().selected shouldBe CreatureSelection.Monster(api.createdId)
        viewModel.loaded().monsters.map { it.name } shouldContainExactly listOf("Zombie", "Ghoul", "Monster")
    }

    test("a refresh should reload the lists, keeping the selection unless another one is given") {
        val api = FakeCreatureApi()
        val viewModel = CreaturesViewModel(api, campaignId)
        val ghoul = CreatureSelection.Monster(api.sheets.entries.first { it.value.name == "Ghoul" }.key)
        viewModel.handle(CreaturesAction.Select(ghoul))

        api.createCharacterSheet(campaignId, ghoul.monsterSheetId)
        viewModel.handle(CreaturesAction.Refresh())
        viewModel.loaded().selected shouldBe ghoul
        viewModel.loaded().characters.size shouldBe 1

        viewModel.handle(CreaturesAction.Refresh(select = CreatureSelection.Character(api.createdCharacterId)))
        viewModel.loaded().selected shouldBe CreatureSelection.Character(api.createdCharacterId)
    }
})
