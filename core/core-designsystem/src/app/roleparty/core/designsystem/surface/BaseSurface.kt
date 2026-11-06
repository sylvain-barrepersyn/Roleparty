package app.roleparty.core.designsystem.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.roleparty.core.designsystem.elevation.LocalElevation
import app.roleparty.core.designsystem.elevation.LocalElevationLevel

@Composable
fun BaseSurface(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val elevation = LocalElevation.current

    CompositionLocalProvider(
        LocalElevationLevel provides 0,
        LocalSurfaceBorderColor provides elevation.borderColor(0),
    ) {
        Box(
            modifier = modifier.background(elevation.backgroundColor(0)),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}
