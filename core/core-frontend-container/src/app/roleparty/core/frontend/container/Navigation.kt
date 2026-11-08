package app.roleparty.core.frontend.container

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

private const val TRANSITION_MILLIS = 300

@Composable
fun <Key : Any> Navigation(
    backStack: List<Key>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Key) -> Unit,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = onBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = { fade() },
        popTransitionSpec = { fade() },
        predictivePopTransitionSpec = { fade() },
        entryProvider = { key -> NavEntry(key) { content(key) } },
    )
}

private fun fade(): ContentTransform = fadeIn(tween(TRANSITION_MILLIS)) togetherWith fadeOut(tween(TRANSITION_MILLIS))
