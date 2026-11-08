package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.contract.web.ChallengeRatingName
import app.roleparty.campaign.creature.contract.web.CreatureSizeName
import app.roleparty.campaign.creature.contract.web.CreatureTypeName
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.MovementModeName
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetsAction
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetsState
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetsViewModel
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

private fun sheet(name: String) = MonsterSheetBody(
    name = name,
    imageId = null,
    size = CreatureSizeName.MEDIUM,
    type = CreatureTypeName.UNDEAD,
    challengeRating = ChallengeRatingName.CR_1,
    armorClass = MonsterSheetBody.ArmorClassBody(12),
    hitPoints = MonsterSheetBody.HitPointsBody(22, "5d8"),
    speeds = mapOf(MovementModeName.WALK to 30),
    abilities = MonsterSheetBody.AbilitiesBody(10, 14, 10, 10, 10, 10),
    passivePerception = 10,
    languages = MonsterSheetBody.LanguagesBody(emptySet()),
    traits = emptyList(),
    actions = emptyList(),
    bonusActions = emptyList(),
    reactions = emptyList(),
    legendaryActions = MonsterSheetBody.LegendaryActionsBody("", emptyList()),
)

private class FakeCreatureApi : CreatureApi {

    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null
    val sheets = mutableMapOf(MonsterSheetId.generate() to sheet("Zombie"), MonsterSheetId.generate() to sheet("Ghoul"))
    val createdId: MonsterSheetId = MonsterSheetId.generate()

    private fun <T> answer(call: String, value: () -> T): Outcome<T, BackendFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value())
    }

    override suspend fun monsterSheets(campaignId: CampaignId) =
        answer("list") { sheets.map { MonsterSheetOverviewResponseBody(it.key, it.value.name, it.value.imageId, it.value.type, it.value.size, it.value.challengeRating) } }

    override suspend fun monsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId) = answer("get ${sheets.getValue(monsterSheetId).name}") {
        sheets.getValue(monsterSheetId)
    }

    override suspend fun createMonsterSheet(campaignId: CampaignId) = answer("create") { createdId.also { sheets[it] = sheet("Monster") } }

    override suspend fun updateMonsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId, sheet: MonsterSheetBody) =
        answer("update ${sheet.name}") { sheets[monsterSheetId] = sheet }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MonsterSheetsViewModelTest : FunSpec({

    val campaignId = CampaignId.generate()

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun MonsterSheetsViewModel.loaded() = state.value.shouldBeInstanceOf<MonsterSheetsState.Loaded>()

    test("monster sheets should be listed by name, and a selected one opened") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetsViewModel(api, campaignId)
        viewModel.loaded().overviews.map { it.name } shouldContainExactly listOf("Ghoul", "Zombie")

        viewModel.handle(MonsterSheetsAction.Select(viewModel.loaded().overviews.first().id))

        val editor = viewModel.loaded().editor.shouldNotBeNull()
        editor.sheet shouldBe sheet("Ghoul")
        editor.dirty shouldBe false
    }

    test("a sheet should only change once in edition, and go back to reading when saved") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetsViewModel(api, campaignId)
        val imageId = ImageId.generate()
        viewModel.handle(MonsterSheetsAction.Select(viewModel.loaded().overviews.last().id))

        viewModel.handle(MonsterSheetsAction.Change(sheet("Ignored")))
        viewModel.loaded().editor?.sheet shouldBe sheet("Zombie")

        viewModel.handle(MonsterSheetsAction.StartEditing)
        viewModel.handle(MonsterSheetsAction.Change(sheet("Zombie lord").copy(imageId = imageId)))
        viewModel.loaded().editor?.dirty shouldBe true
        viewModel.handle(MonsterSheetsAction.Save)

        val editor = viewModel.loaded().editor.shouldNotBeNull()
        editor.editing shouldBe false
        editor.dirty shouldBe false
        viewModel.loaded().overviews.map { it.name } shouldContainExactly listOf("Ghoul", "Zombie lord")
        viewModel.loaded().overviews.last().imageId shouldBe imageId
        api.calls.last() shouldBe "update Zombie lord"
    }

    test("a cancelled edition should restore the saved sheet") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetsViewModel(api, campaignId)
        viewModel.handle(MonsterSheetsAction.Select(viewModel.loaded().overviews.first().id))

        viewModel.handle(MonsterSheetsAction.StartEditing)
        viewModel.handle(MonsterSheetsAction.Change(sheet("Ghast")))
        viewModel.handle(MonsterSheetsAction.CancelEditing)

        val editor = viewModel.loaded().editor.shouldNotBeNull()
        editor.sheet shouldBe sheet("Ghoul")
        editor.editing shouldBe false
        api.calls.none { it.startsWith("update") } shouldBe true
    }

    test("a refused sheet should stay in edition and show why") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetsViewModel(api, campaignId)
        viewModel.handle(MonsterSheetsAction.Select(viewModel.loaded().overviews.first().id))
        viewModel.handle(MonsterSheetsAction.StartEditing)
        viewModel.handle(MonsterSheetsAction.Change(sheet("")))

        api.failure = BackendFailure.Rejected("creature.invalid-monster-sheet", "The name is missing.")
        viewModel.handle(MonsterSheetsAction.Save)

        val editor = viewModel.loaded().editor.shouldNotBeNull()
        editor.editing shouldBe true
        editor.dirty shouldBe true
        editor.saving shouldBe false
        editor.failure shouldBe BackendFailure.Rejected("creature.invalid-monster-sheet", "The name is missing.")
    }

    test("a duplicated sheet should be created with the same content, then opened") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetsViewModel(api, campaignId)
        viewModel.handle(MonsterSheetsAction.Select(viewModel.loaded().overviews.first().id))

        viewModel.handle(MonsterSheetsAction.Duplicate)

        viewModel.loaded().editor?.monsterSheetId shouldBe api.createdId
        viewModel.loaded().editor?.sheet shouldBe sheet("Ghoul")
        viewModel.loaded().overviews.map { it.name } shouldContainExactly listOf("Ghoul", "Ghoul", "Zombie")
    }

    test("a created monster sheet should be listed and opened") {
        val api = FakeCreatureApi()
        val viewModel = MonsterSheetsViewModel(api, campaignId)

        viewModel.handle(MonsterSheetsAction.Create)

        viewModel.loaded().editor?.monsterSheetId shouldBe api.createdId
        viewModel.loaded().editor?.sheet shouldBe sheet("Monster")
        api.calls shouldContainExactly listOf("list", "create", "list", "get Monster")
    }
})
