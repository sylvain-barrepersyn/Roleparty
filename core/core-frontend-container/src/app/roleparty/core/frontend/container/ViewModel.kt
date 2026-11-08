package app.roleparty.core.frontend.container

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ViewModelState

interface ViewModelAction

interface ViewModelEvent

abstract class ViewModel<State : ViewModelState, Action : ViewModelAction, Event : ViewModelEvent>(
    initialState: State,
) : androidx.lifecycle.ViewModel() {

    private val mutableState = MutableStateFlow(initialState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<Event>(extraBufferCapacity = EVENT_BUFFER)
    val events: SharedFlow<Event> = mutableEvents.asSharedFlow()

    private var attached = false

    init {
        Convention.check(javaClass, ViewModel::class.java, "ViewModel")
    }

    abstract fun handle(action: Action)

    internal fun attach() {
        check(!attached) { "${javaClass.simpleName} is shared: declare it with viewModel { } or viewModelOf(), never single" }
        attached = true
    }

    protected fun update(transform: (State) -> State) = mutableState.update(transform)

    protected fun emit(event: Event) {
        viewModelScope.launch { mutableEvents.emit(event) }
    }

    private companion object {
        const val EVENT_BUFFER = 16
    }
}
