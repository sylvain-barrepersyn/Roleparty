package app.roleparty.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalColors = staticCompositionLocalOf { lightColors }

private val LocalTypography = staticCompositionLocalOf<Typography> { error("No typography provided") }

@Composable
internal fun provideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColors else lightColors
    val typography = rememberAppTypography()

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides typography,
        content = content
    )
}

object Theme {
    val colors @Composable get() = LocalColors.current
    val typography @Composable get() = LocalTypography.current
}