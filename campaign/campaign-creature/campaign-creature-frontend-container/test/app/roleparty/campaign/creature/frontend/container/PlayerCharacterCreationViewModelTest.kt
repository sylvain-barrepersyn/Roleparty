package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.creature.contract.web.AbilityName
import app.roleparty.campaign.creature.contract.web.AlignmentName
import app.roleparty.campaign.creature.contract.web.HitDieName
import app.roleparty.campaign.creature.contract.web.SkillName
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.AbilityScoreMethod
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.PlayerCharacterCreationStep
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.PlayerCharacterDraft
import app.roleparty.campaign.creature.frontend.container.playercharacter.creation.PlayerCharacterCreationAction
import app.roleparty.campaign.creature.frontend.container.playercharacter.creation.PlayerCharacterCreationEvent
import app.roleparty.campaign.creature.frontend.container.playercharacter.creation.PlayerCharacterCreationState
import app.roleparty.campaign.creature.frontend.container.playercharacter.creation.PlayerCharacterCreationViewModel
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.random.Random

private val complete = PlayerCharacterDraft(
    className = "Runesmith",
    hitDie = HitDieName.D10,
    savingThrows = setOf(AbilityName.STRENGTH, AbilityName.CONSTITUTION),
    classSkills = setOf(SkillName.ATHLETICS),
    backgroundName = "Mountain guide",
    backgroundSkills = setOf(SkillName.PERCEPTION),
    speciesName = "Stone dwarf",
    languages = "Common, Dwarvish, ",
    baseScores = mapOf(
        AbilityName.STRENGTH to 15, AbilityName.DEXTERITY to 13, AbilityName.CONSTITUTION to 14,
        AbilityName.INTELLIGENCE to 10, AbilityName.WISDOM to 12, AbilityName.CHARISMA to 8,
    ),
    plusTwo = AbilityName.CONSTITUTION,
    plusOne = AbilityName.STRENGTH,
    alignment = AlignmentName.LAWFUL_GOOD,
    name = " Gimli ",
    imageId = ImageId.generate(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerCharacterCreationViewModelTest : FunSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun PlayerCharacterCreationViewModel.form() = state.value.shouldBeInstanceOf<PlayerCharacterCreationState.Form>()

    test("a step should only be reachable once the previous ones are complete") {
        val viewModel = PlayerCharacterCreationViewModel(FakeCreatureApi(), FakeCreatureApi().campaignId)

        viewModel.handle(PlayerCharacterCreationAction.ShowStep(PlayerCharacterCreationStep.ORIGIN))
        viewModel.form().step shouldBe PlayerCharacterCreationStep.CLASS

        viewModel.handle(PlayerCharacterCreationAction.ChangeDraft(PlayerCharacterDraft(className = "Runesmith", hitDie = HitDieName.D10)))
        viewModel.handle(PlayerCharacterCreationAction.ShowStep(PlayerCharacterCreationStep.ORIGIN))
        viewModel.handle(PlayerCharacterCreationAction.ShowStep(PlayerCharacterCreationStep.ABILITY_SCORES))
        viewModel.form().step shouldBe PlayerCharacterCreationStep.ORIGIN
    }

    test("rolled scores should be six sums of the three best of four d6, left to assign") {
        val viewModel = PlayerCharacterCreationViewModel(FakeCreatureApi(), FakeCreatureApi().campaignId, Random(42))
        viewModel.handle(PlayerCharacterCreationAction.ChangeDraft(complete))

        viewModel.handle(PlayerCharacterCreationAction.RollAbilityScores)

        val draft = viewModel.form().draft
        draft.scoreMethod shouldBe AbilityScoreMethod.RANDOM
        draft.rolledScores shouldHaveSize 6
        draft.rolledScores.forEach { it shouldBeInRange 3..18 }
        draft.baseScores shouldBe emptyMap()
        draft.isComplete(PlayerCharacterCreationStep.ABILITY_SCORES) shouldBe false
    }

    test("a complete character should be created with its adjusted scores and skills, then reported") {
        val api = FakeCreatureApi()
        val viewModel = PlayerCharacterCreationViewModel(api, api.campaignId)
        val events = viewModel.recordedEvents()

        viewModel.handle(PlayerCharacterCreationAction.ChangeDraft(complete))
        viewModel.handle(PlayerCharacterCreationAction.Submit)

        val body = api.createdPlayerCharacters.single()
        body.name shouldBe "Gimli"
        body.className shouldBe "Runesmith"
        body.abilityScores.constitution shouldBe 16
        body.abilityScores.strength shouldBe 16
        body.skills shouldBe setOf(SkillName.ATHLETICS, SkillName.PERCEPTION)
        body.languages shouldBe listOf("Common", "Dwarvish")
        events shouldContainExactly listOf(PlayerCharacterCreationEvent.PlayerCharacterCreated(api.createdPlayerCharacterId))
    }

    test("an incomplete character should not be sent, and a refusal should be shown") {
        val api = FakeCreatureApi()
        val viewModel = PlayerCharacterCreationViewModel(api, api.campaignId)

        viewModel.handle(PlayerCharacterCreationAction.ChangeDraft(complete.copy(imageId = null)))
        viewModel.handle(PlayerCharacterCreationAction.Submit)
        api.calls.shouldBeEmpty()

        api.failure = BackendFailure.Rejected("campaign.creature.blank-class-name", "Name the class.")
        viewModel.handle(PlayerCharacterCreationAction.ChangeDraft(complete))
        viewModel.handle(PlayerCharacterCreationAction.Submit)

        viewModel.form().submitting shouldBe false
        viewModel.form().failure shouldBe BackendFailure.Rejected("campaign.creature.blank-class-name", "Name the class.")
    }
})

class PlayerCharacterDraftTest : FunSpec({

    test("the standard array should be assigned once each") {
        val draft = complete.copy(baseScores = complete.baseScores + (AbilityName.CHARISMA to 15))

        draft.unassignedScores shouldBe listOf(8)
        draft.isComplete(PlayerCharacterCreationStep.ABILITY_SCORES) shouldBe false
        complete.isComplete(PlayerCharacterCreationStep.ABILITY_SCORES) shouldBe true
    }

    test("the point cost should start at 8 everywhere and stay within 27 points") {
        val draft = complete.withScoreMethod(AbilityScoreMethod.POINT_BUY)
        draft.pointsSpent shouldBe 0

        val spent = draft.copy(baseScores = draft.baseScores + mapOf(AbilityName.STRENGTH to 15, AbilityName.DEXTERITY to 15, AbilityName.CONSTITUTION to 15))
        spent.pointsSpent shouldBe 27
        spent.isComplete(PlayerCharacterCreationStep.ABILITY_SCORES) shouldBe true

        spent.copy(baseScores = spent.baseScores + (AbilityName.WISDOM to 9)).isComplete(PlayerCharacterCreationStep.ABILITY_SCORES) shouldBe false
    }

    test("the background adjustment should never raise a score above 20, and need three abilities when spread") {
        val draft = complete.withScoreMethod(AbilityScoreMethod.POINT_BUY).copy(plusTwo = AbilityName.WISDOM, plusOne = AbilityName.WISDOM)
        draft.isComplete(PlayerCharacterCreationStep.ABILITY_SCORES) shouldBe false

        val rolled = complete.copy(
            scoreMethod = AbilityScoreMethod.RANDOM,
            rolledScores = listOf(19, 18, 10, 10, 10, 10),
            baseScores = mapOf(
                AbilityName.STRENGTH to 19, AbilityName.DEXTERITY to 18, AbilityName.CONSTITUTION to 10,
                AbilityName.INTELLIGENCE to 10, AbilityName.WISDOM to 10, AbilityName.CHARISMA to 10,
            ),
            plusTwo = AbilityName.STRENGTH,
            plusOne = AbilityName.DEXTERITY,
        )
        rolled.score(AbilityName.STRENGTH) shouldBe 20
        rolled.score(AbilityName.DEXTERITY) shouldBe 19

        val spread = rolled.copy(backgroundBonus = app.roleparty.campaign.creature.frontend.component.playercharacter.creation.BackgroundBonus.PLUS_ONE_TO_THREE, plusOnes = setOf(AbilityName.WISDOM))
        spread.isComplete(PlayerCharacterCreationStep.ABILITY_SCORES) shouldBe false
        spread.copy(plusOnes = setOf(AbilityName.WISDOM, AbilityName.CHARISMA, AbilityName.INTELLIGENCE)).score(AbilityName.WISDOM) shouldBe 11
    }

    test("hit points at level 1 should be the hit die plus the Constitution modifier") {
        complete.maxHitPoints shouldBe 13
    }
})
