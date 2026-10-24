package app.roleparty.core.designsystem.components.button

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonSize(
    internal val height: Dp,
    internal val horizontalPadding: Dp,
    internal val verticalPadding: Dp,
    internal val leadingSpacing: Dp,
    internal val trailingSpacing: Dp,
    internal val iconSize: Dp,
    internal val indicatorIconSize: Dp,
    internal val indicatorSpacing: Dp,
) {
    HUGE(
        height = 56.dp,
        horizontalPadding = 14.dp,
        verticalPadding = 12.dp,
        leadingSpacing = 8.dp,
        trailingSpacing = 6.dp,
        iconSize = 28.dp,
        indicatorIconSize = 20.dp,
        indicatorSpacing = 8.dp
    ),
    LARGE(
        height = 48.dp,
        horizontalPadding = 10.dp,
        verticalPadding = 10.dp,
        leadingSpacing = 6.dp,
        trailingSpacing = 8.dp,
        iconSize = 24.dp,
        indicatorIconSize = 16.dp,
        indicatorSpacing = 8.dp
    ),
    NORMAL(
        height = 40.dp,
        horizontalPadding = 8.dp,
        verticalPadding = 8.dp,
        leadingSpacing = 4.dp,
        trailingSpacing = 6.dp,
        iconSize = 24.dp,
        indicatorIconSize = 16.dp,
        indicatorSpacing = 8.dp
    ),
    SMALL(
        height = 32.dp,
        horizontalPadding = 6.dp,
        verticalPadding = 4.dp,
        leadingSpacing = 4.dp,
        trailingSpacing = 4.dp,
        iconSize = 20.dp,
        indicatorIconSize = 14.dp,
        indicatorSpacing = 4.dp
    ),
    TINY(
        height = 24.dp,
        horizontalPadding = 5.dp,
        verticalPadding = 4.dp,
        leadingSpacing = 2.dp,
        trailingSpacing = 2.dp,
        iconSize = 14.dp,
        indicatorIconSize = 12.dp,
        indicatorSpacing = 2.dp
    ),
}
