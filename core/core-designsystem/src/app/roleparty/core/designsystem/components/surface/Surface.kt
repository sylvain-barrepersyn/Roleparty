package app.roleparty.core.designsystem.components.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.roleparty.core.designsystem.elevation.ElevationTokens
import app.roleparty.core.designsystem.elevation.LocalElevationLevel

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    elevationOffset: Int = 1,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val currentLevel = LocalElevationLevel.current
    val newLevel = currentLevel + elevationOffset

    val backgroundColor = remember(newLevel) {
        ElevationTokens.backgroundColor(newLevel)
    }

    CompositionLocalProvider(LocalElevationLevel provides newLevel) {
        Box(
            modifier = Modifier.background(backgroundColor).then(modifier),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}
