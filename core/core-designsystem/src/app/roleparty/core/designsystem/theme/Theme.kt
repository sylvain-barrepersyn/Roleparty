package app.roleparty.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.elevation.LocalElevation
import app.roleparty.core.designsystem.elevation.darkElevation
import app.roleparty.core.designsystem.elevation.lightElevation

private val LocalColors = staticCompositionLocalOf<Colors> { error("No colors provided, wrap the UI in RolepartyTheme") }

private val LocalTypography = staticCompositionLocalOf<Typography> { error("No typography provided, wrap the UI in RolepartyTheme") }

@Composable
fun RolepartyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) darkColors else lightColors

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalElevation provides if (darkTheme) darkElevation else lightElevation,
        LocalTypography provides rememberAppTypography(colors),
        content = content,
    )
}

object Theme {
    val colors: Colors @Composable get() = LocalColors.current
    val typography: Typography @Composable get() = LocalTypography.current
    val shapes = Shapes
    val borderWidth: Dp = 2.dp
}
