package app.roleparty.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.roleparty.core.designsystem.elevation.ElevationTokens
import app.roleparty.core.designsystem.elevation.LocalElevationLevel
import app.roleparty.core.designsystem.theme.provideTheme

@Composable
fun BaseSurface(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    provideTheme {
        CompositionLocalProvider(LocalElevationLevel provides 0) {
            val backgroundColor = ElevationTokens.backgroundColor(0)

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = contentAlignment,
                content = content
            )
        }
    }
}
