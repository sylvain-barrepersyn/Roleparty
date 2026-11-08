package app.roleparty.core.frontend.container

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed interface CounterState : ViewModelState {
    data class Counting(val name: String, val count: Int) : CounterState
}

sealed interface CounterAction : ViewModelAction {
    data object Increment : CounterAction
}

sealed interface CounterEvent : ViewModelEvent {
    data class GoalReached(val name: String) : CounterEvent
}

class CounterViewModel(name: String, private val goal: Int) :
    ViewModel<CounterState, CounterAction, CounterEvent>(CounterState.Counting(name, 0)) {

    override fun handle(action: CounterAction) {
        when (action) {
            CounterAction.Increment -> {
                update { (it as CounterState.Counting).copy(count = it.count + 1) }
                val counting = state.value as CounterState.Counting
                if (counting.count == goal) emit(CounterEvent.GoalReached(counting.name))
            }
        }
    }
}

class CounterContainer(name: String, private val onGoalReached: (String) -> Unit = {}) :
    Container<CounterState, CounterAction, CounterEvent, CounterViewModel>(CounterViewModel::class, name) {

    @Composable
    override fun Content(state: CounterState, handle: (CounterAction) -> Unit) = when (state) {
        is CounterState.Counting -> Counter(state.name, state.count, onIncrement = { handle(CounterAction.Increment) })
    }

    override fun onEvent(event: CounterEvent) = when (event) {
        is CounterEvent.GoalReached -> onGoalReached(event.name)
    }
}

@Composable
private fun Counter(name: String, count: Int, onIncrement: () -> Unit) {
    Column {
        BasicText("$name: $count")
        BasicText("Increment $name", Modifier.clickable(onClick = onIncrement))
    }
}
