package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetAction
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetEvent
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetState
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetViewModel
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
class MonsterSheetViewModelTest : FunSpec({

    val campaignId = CampaignId.generate()

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun MonsterSheetViewModel.loaded() = state.value.shouldBeInstanceOf<MonsterSheetState.Loaded>()

    fun zombieOf(api: FakeCreatureApi) = api.sheets.entries.first { it.value.name == "Zombie" }.key

    test("a sheet should only change in edition, then be saved and reported") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetViewModel(api, campaignId, zombieOf(api))
        val events = viewModel.recordedEvents()
        val imageId = ImageId.generate()

        viewModel.handle(MonsterSheetAction.Change(sheet("Ignored")))
        viewModel.loaded().sheet shouldBe sheet("Zombie")

        viewModel.handle(MonsterSheetAction.StartEditing)
        viewModel.handle(MonsterSheetAction.Change(sheet("Zombie lord").copy(imageId = imageId)))
        viewModel.loaded().dirty shouldBe true
        viewModel.handle(MonsterSheetAction.Save)

        viewModel.loaded().editing shouldBe false
        viewModel.loaded().dirty shouldBe false
        api.calls.last() shouldBe "update Zombie lord"
        events shouldContainExactly listOf(MonsterSheetEvent.Saved)
    }

    test("a cancelled edition should restore the saved sheet") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetViewModel(api, campaignId, zombieOf(api))

        viewModel.handle(MonsterSheetAction.StartEditing)
        viewModel.handle(MonsterSheetAction.Change(sheet("Ghast")))
        viewModel.handle(MonsterSheetAction.CancelEditing)

        viewModel.loaded().sheet shouldBe sheet("Zombie")
        viewModel.loaded().editing shouldBe false
        api.calls.none { it.startsWith("update") } shouldBe true
    }

    test("a refused sheet should stay in edition and show why") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetViewModel(api, campaignId, zombieOf(api))
        viewModel.handle(MonsterSheetAction.StartEditing)
        viewModel.handle(MonsterSheetAction.Change(sheet("")))

        api.failure = BackendFailure.Rejected("campaign.creature.invalid-name", "The name is missing.")
        viewModel.handle(MonsterSheetAction.Save)

        viewModel.loaded().editing shouldBe true
        viewModel.loaded().saving shouldBe false
        viewModel.loaded().failure shouldBe BackendFailure.Rejected("campaign.creature.invalid-name", "The name is missing.")
    }

    test("a duplicate should be created with the same content and reported") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetViewModel(api, campaignId, zombieOf(api))
        val events = viewModel.recordedEvents()

        viewModel.handle(MonsterSheetAction.Duplicate)

        api.sheets.getValue(api.createdId) shouldBe sheet("Zombie")
        events shouldContainExactly listOf(MonsterSheetEvent.Duplicated(api.createdId))
    }

    test("a player character should be created from the monster and reported") {
        val api = FakeCreatureApi()
        val zombie = zombieOf(api)
        val viewModel = MonsterSheetViewModel(api, campaignId, zombie)
        val events = viewModel.recordedEvents()

        viewModel.handle(MonsterSheetAction.CreateCharacter)

        api.characters.getValue(api.createdCharacterId).monsterSheetId shouldBe zombie
        events shouldContainExactly listOf(MonsterSheetEvent.CharacterCreated(api.createdCharacterId))
    }
})
