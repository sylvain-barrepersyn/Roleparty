package app.roleparty.core.designsystem.theme

import androidx.compose.ui.graphics.Color

@ConsistentCopyVisibility
data class Colors internal constructor(
    val primary: Color,
    val error: Color,
    val textPrimary: Color,
    val textSecondary: Color,
)

internal val darkColors = Colors(
    primary = Color(0xFFF89B5D),
    error = Color(0xFFF09699),
    textPrimary = Color(0xFFE6E6E6),
    textSecondary = Color(0xFFAEAEAE),
)

internal val lightColors = Colors(
    primary = Color(0xFFA94C0A),
    error = Color(0xFFC42A2F),
    textPrimary = Color(0xFF1F1F1F),
    textSecondary = Color(0xFF5C5C5C),
)

internal fun Color.disabled(): Color = copy(alpha = 0.5f)
