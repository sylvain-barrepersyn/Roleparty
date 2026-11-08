package app.roleparty.core.frontend.container

import androidx.compose.runtime.Composable

class MisnamedCounter : ViewModel<CounterState, CounterAction, CounterEvent>(CounterState.Counting("misnamed", 0)) {
    override fun handle(action: CounterAction) = Unit
}

sealed interface OtherState : ViewModelState {
    data object Idle : OtherState
}

class MismatchedViewModel : ViewModel<OtherState, CounterAction, CounterEvent>(OtherState.Idle) {
    override fun handle(action: CounterAction) = Unit
}

data class LooseState(val value: Int) : ViewModelState

sealed interface LooseAction : ViewModelAction

sealed interface LooseEvent : ViewModelEvent

class LooseViewModel : ViewModel<LooseState, LooseAction, LooseEvent>(LooseState(0)) {
    override fun handle(action: LooseAction) = Unit
}

sealed interface QuietState : ViewModelState {
    data object Idle : QuietState
}

sealed interface QuietAction : ViewModelAction {
    data object Wait : QuietAction
}

sealed interface QuietEvent : ViewModelEvent

class QuietViewModel : ViewModel<QuietState, QuietAction, QuietEvent>(QuietState.Idle) {
    override fun handle(action: QuietAction) = Unit
}

class OtherContainer : Container<CounterState, CounterAction, CounterEvent, CounterViewModel>(CounterViewModel::class) {
    @Composable
    override fun Content(state: CounterState, handle: (CounterAction) -> Unit) = Unit
}
