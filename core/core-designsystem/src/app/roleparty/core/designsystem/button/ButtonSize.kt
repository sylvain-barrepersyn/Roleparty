package app.roleparty.core.designsystem.button

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonSize(
    internal val height: Dp,
    internal val horizontalPadding: Dp,
    internal val leadingIconSize: Dp,
    internal val trailingIconSize: Dp,
    internal val leadingSpacing: Dp,
    internal val trailingSpacing: Dp,
) {
    HUGE(
        height = 56.dp,
        horizontalPadding = 14.dp,
        leadingIconSize = 28.dp,
        trailingIconSize = 20.dp,
        leadingSpacing = 8.dp,
        trailingSpacing = 8.dp,
    ),
    LARGE(
        height = 48.dp,
        horizontalPadding = 10.dp,
        leadingIconSize = 24.dp,
        trailingIconSize = 16.dp,
        leadingSpacing = 6.dp,
        trailingSpacing = 8.dp,
    ),
    NORMAL(
        height = 40.dp,
        horizontalPadding = 8.dp,
        leadingIconSize = 24.dp,
        trailingIconSize = 16.dp,
        leadingSpacing = 4.dp,
        trailingSpacing = 8.dp,
    ),
    SMALL(
        height = 32.dp,
        horizontalPadding = 6.dp,
        leadingIconSize = 20.dp,
        trailingIconSize = 14.dp,
        leadingSpacing = 4.dp,
        trailingSpacing = 4.dp,
    ),
    TINY(
        height = 24.dp,
        horizontalPadding = 5.dp,
        leadingIconSize = 14.dp,
        trailingIconSize = 12.dp,
        leadingSpacing = 2.dp,
        trailingSpacing = 2.dp,
    ),
}
