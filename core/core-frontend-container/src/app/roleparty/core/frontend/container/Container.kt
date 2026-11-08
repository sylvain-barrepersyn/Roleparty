package app.roleparty.core.frontend.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.reflect.KClass
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

abstract class Container<State : ViewModelState, Action : ViewModelAction, Event : ViewModelEvent, VM : ViewModel<State, Action, Event>>(
    private val viewModelClass: KClass<VM>,
    private vararg val parameters: Any,
) {

    init {
        Convention.check(javaClass, Container::class.java, "Container")
    }

    @Composable
    protected abstract fun Content(state: State, handle: (Action) -> Unit)

    protected open fun onEvent(event: Event) {}

    @Composable
    fun Render() {
        val viewModel = viewModel()
        val state by viewModel.state.collectAsState()
        val onEvent by rememberUpdatedState<(Event) -> Unit>(::onEvent)
        LaunchedEffect(viewModel) { viewModel.events.collect { onEvent(it) } }
        Content(state, viewModel::handle)
    }

    @Composable
    private fun viewModel(): VM {
        val scope = currentKoinScope()
        val factory = remember(scope) { KoinFactory(scope) }
        return viewModel(
            modelClass = viewModelClass,
            key = listOf(viewModelClass.qualifiedName, *parameters).joinToString(":"),
            factory = factory,
        )
    }

    private inner class KoinFactory(private val scope: Scope) : ViewModelProvider.Factory {

        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            val viewModel: T = scope.get(modelClass, null) { parametersOf(*parameters) }
            (viewModel as ViewModel<*, *, *>).attach()
            return viewModel
        }
    }
}
