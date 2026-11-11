package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.creature.frontend.container.playercharacter.PlayerCharacterSelectionAction
import app.roleparty.campaign.creature.frontend.container.playercharacter.PlayerCharacterSelectionEvent
import app.roleparty.campaign.creature.frontend.container.playercharacter.PlayerCharacterSelectionState
import app.roleparty.campaign.creature.frontend.container.playercharacter.PlayerCharacterSelectionViewModel
import app.roleparty.core.ktor.client.BackendFailure
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerCharacterSelectionViewModelTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    test("the characters of the player should be listed, and the chosen one opened") {
        val api = FakeCreatureApi()
        val viewModel = PlayerCharacterSelectionViewModel(api, api.campaignId)
        val events = viewModel.recordedEvents()

        viewModel.handle(PlayerCharacterSelectionAction.Refresh)
        viewModel.handle(PlayerCharacterSelectionAction.OpenPlayerCharacter(api.playerCharacters.single().id))
        viewModel.handle(PlayerCharacterSelectionAction.CreatePlayerCharacter)
        viewModel.handle(PlayerCharacterSelectionAction.Exit)

        viewModel.state.value shouldBe PlayerCharacterSelectionState.Loaded(api.playerCharacters)
        events shouldContainExactly listOf(
            PlayerCharacterSelectionEvent.PlayerCharacterOpened(api.playerCharacters.single().id),
            PlayerCharacterSelectionEvent.PlayerCharacterCreationRequested,
            PlayerCharacterSelectionEvent.Exited,
        )
    }

    test("a player without character should be sent once to the character creation, then see the characters created") {
        val api = FakeCreatureApi().apply { failure = BackendFailure.Unreachable; playerCharacters = emptyList() }
        val viewModel = PlayerCharacterSelectionViewModel(api, api.campaignId)
        val events = viewModel.recordedEvents()
        viewModel.handle(PlayerCharacterSelectionAction.Refresh)
        viewModel.state.value shouldBe PlayerCharacterSelectionState.Failed(BackendFailure.Unreachable)

        api.failure = null
        viewModel.handle(PlayerCharacterSelectionAction.Refresh)
        viewModel.state.value shouldBe PlayerCharacterSelectionState.Loaded(emptyList())

        viewModel.handle(PlayerCharacterSelectionAction.Refresh)
        api.playerCharacters = FakeCreatureApi().playerCharacters
        viewModel.handle(PlayerCharacterSelectionAction.Refresh)

        viewModel.state.value shouldBe PlayerCharacterSelectionState.Loaded(api.playerCharacters)
        events shouldContainExactly listOf(PlayerCharacterSelectionEvent.PlayerCharacterCreationRequested)
    }
})
