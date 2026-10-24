package app.roleparty.core.designsystem.elevation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalElevationLevel = staticCompositionLocalOf { 0 }

internal object ElevationTokens {
    private const val INITIAL_BACKGROUND_LIGHTNESS = 0.08f
    private const val INITIAL_BORDER_LIGHTNESS = 0.10f
    private const val LIGHTNESS_STEP = 0.06f

    fun backgroundColor(level: Int): Color {
        val lightness = (INITIAL_BACKGROUND_LIGHTNESS + LIGHTNESS_STEP * level).coerceIn(0f, 1f)
        return Color.hsl(0f, 0f, lightness)
    }

    fun borderColor(level: Int): Color {
        val lightness = (INITIAL_BORDER_LIGHTNESS + LIGHTNESS_STEP * level).coerceIn(0f, 1f)
        return Color.hsl(0f, 0f, lightness)
    }
}
