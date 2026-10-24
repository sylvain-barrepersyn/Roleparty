package app.roleparty.core.designsystem.components.surface

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.elevation.ElevationTokens
import app.roleparty.core.designsystem.elevation.LocalElevationLevel

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val currentLevel = LocalElevationLevel.current
    val newLevel = currentLevel + 1

    val baseBackgroundColor = remember(newLevel) { ElevationTokens.backgroundColor(newLevel) }

    val targetBackgroundColor = when {
        isPressed -> baseBackgroundColor.copy(alpha = 0.80f)
        isHovered -> baseBackgroundColor.copy(alpha = 0.90f)
        else -> baseBackgroundColor
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(durationMillis = 120)
    )

    val borderColor = remember(newLevel) { ElevationTokens.borderColor(newLevel) }

    CompositionLocalProvider(LocalElevationLevel provides newLevel) {
        Box(
            modifier = modifier
                .background(backgroundColor, shape)
                .border(2.dp, borderColor, shape)
                .let {
                    if (onClick != null && enabled) it.clickable(
                        interactionSource = interactionSource,
                        enabled = enabled,
                        indication = null,
                        onClick = onClick
                    ) else it
                }
                .let { if (onClick != null && enabled) it.pointerHoverIcon(PointerIcon.Hand) else it },
            contentAlignment = contentAlignment,
            content = content
        )
    }
}