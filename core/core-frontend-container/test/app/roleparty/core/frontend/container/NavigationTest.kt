package app.roleparty.core.frontend.container

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.kotest.core.spec.style.FunSpec
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class NavigationTest : FunSpec({

    test("a popped screen should lose its view model, while the screen below keeps its own") {
        val backStack = mutableStateListOf("goblins")
        val owner = object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }

        runComposeUiTest {
            setContent {
                KoinIsolatedContext(koinApplication { modules(module { viewModel { parameters -> CounterViewModel(parameters.get(), goal = 9) } }) }) {
                    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                        Navigation(backStack = backStack, onBack = { backStack.removeAt(backStack.lastIndex) }) { name ->
                            CounterContainer(name).Render()
                        }
                    }
                }
            }

            onNodeWithText("Increment goblins").performClick()
            backStack.add("orcs")
            waitForIdle()
            onNodeWithText("Increment orcs").performClick()
            onNodeWithText("orcs: 1").assertExists()

            backStack.removeAt(backStack.lastIndex)
            waitForIdle()
            onNodeWithText("goblins: 1").assertExists()

            backStack.add("orcs")
            waitForIdle()
            onNodeWithText("orcs: 0").assertExists()
        }
    }
})
