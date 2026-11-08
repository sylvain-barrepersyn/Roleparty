package app.roleparty.core.frontend.container

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest : FunSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    test("an action should update the state and emit an event") {
        val viewModel = CounterViewModel("goblins", goal = 1)
        val event = async { viewModel.events.first() }
        yield()

        viewModel.handle(CounterAction.Increment)

        viewModel.state.value shouldBe CounterState.Counting("goblins", 1)
        event.await() shouldBe CounterEvent.GoalReached("goblins")
    }

    test("a view model should be named after its screen") {
        shouldThrow<IllegalArgumentException> { MisnamedCounter() }.message shouldContain "<Screen>ViewModel"
    }

    test("state, action and event should be named after the screen") {
        shouldThrow<IllegalArgumentException> { MismatchedViewModel() }.message shouldContain "must be named MismatchedState"
    }

    test("state, action and event should be sealed interfaces") {
        shouldThrow<IllegalArgumentException> { LooseViewModel() }.message shouldContain "must be a sealed interface"
    }

    test("a screen without any event should still be accepted") {
        QuietViewModel().state.value shouldBe QuietState.Idle
    }
})
