package app.roleparty.core.designsystem.elevation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalElevationLevel = staticCompositionLocalOf { 0 }

internal val LocalElevation = staticCompositionLocalOf<Elevation> { error("No elevation provided, wrap the UI in RolepartyTheme") }

internal class Elevation(
    private val initialBackgroundLightness: Float,
    private val backgroundLightnessStep: Float,
    private val initialBorderLightness: Float,
    private val borderLightnessStep: Float,
) {
    fun backgroundColor(level: Int): Color = grayscale(initialBackgroundLightness + backgroundLightnessStep * level)

    fun borderColor(level: Int): Color = grayscale(initialBorderLightness + borderLightnessStep * level)

    private fun grayscale(lightness: Float) = Color.hsl(0f, 0f, lightness.coerceIn(0f, 1f))
}

internal val darkElevation = Elevation(
    initialBackgroundLightness = 0.08f,
    backgroundLightnessStep = 0.06f,
    initialBorderLightness = 0.10f,
    borderLightnessStep = 0.06f,
)

internal val lightElevation = Elevation(
    initialBackgroundLightness = 0.90f,
    backgroundLightnessStep = 0.03f,
    initialBorderLightness = 0.86f,
    borderLightnessStep = -0.03f,
)
