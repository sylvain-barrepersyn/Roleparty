package app.roleparty.core.frontend.container

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class ContainerTest : FunSpec({

    val counterModule = module {
        viewModel { parameters -> CounterViewModel(parameters.get(), goal = 2) }
    }

    fun ComposeUiTest.render(
        store: ViewModelStore = ViewModelStore(),
        koinModule: Module = counterModule,
        content: @Composable () -> Unit,
    ) {
        val owner = object : ViewModelStoreOwner {
            override val viewModelStore = store
        }
        setContent {
            KoinIsolatedContext(koinApplication { modules(koinModule) }) {
                CompositionLocalProvider(LocalViewModelStoreOwner provides owner) { content() }
            }
        }
    }

    test("the content should render the state and report actions to the view model") {
        runComposeUiTest {
            render { CounterContainer("goblins").Render() }

            onNodeWithText("Increment goblins").performClick()

            onNodeWithText("goblins: 1").assertExists()
        }
    }

    test("events should reach the container") {
        val reached = mutableListOf<String>()
        runComposeUiTest {
            render { CounterContainer("goblins", onGoalReached = { reached += it }).Render() }

            repeat(2) { onNodeWithText("Increment goblins").performClick() }
            waitForIdle()

            reached shouldBe listOf("goblins")
        }
    }

    test("each set of parameters should get its own view model") {
        runComposeUiTest {
            render {
                Column {
                    CounterContainer("goblins").Render()
                    CounterContainer("orcs").Render()
                }
            }

            onNodeWithText("Increment goblins").performClick()

            onNodeWithText("goblins: 1").assertExists()
            onNodeWithText("orcs: 0").assertExists()
        }
    }

    test("the view model should be kept in the store and cleared with it") {
        val store = ViewModelStore()
        runComposeUiTest {
            render(store) { CounterContainer("goblins").Render() }
            onNodeWithText("Increment goblins").performClick()
            waitForIdle()

            store.keys().size shouldBe 1
            store.clear()
            store.keys().size shouldBe 0
        }
    }

    test("a view model declared as a singleton should be refused") {
        val singleModule = module { single { parameters -> CounterViewModel(parameters.get(), goal = 2) } }

        shouldThrow<IllegalStateException> {
            runComposeUiTest {
                render(koinModule = singleModule) {
                    Column {
                        CounterContainer("goblins").Render()
                        CounterContainer("orcs").Render()
                    }
                }
            }
        }.message shouldContain "never single"
    }

    test("a container should be named after its view model's screen") {
        shouldThrow<IllegalArgumentException> { OtherContainer() }.message shouldContain "must be named OtherState"
    }
})
