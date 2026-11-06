package app.roleparty.core.designsystem.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import app.roleparty.core.designsystem.elevation.LocalElevation
import app.roleparty.core.designsystem.elevation.LocalElevationLevel
import app.roleparty.core.designsystem.theme.Theme

internal val LocalSurfaceBorderColor = compositionLocalOf<Color> { error("No surface provided, wrap the UI in BaseSurface") }

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = Theme.shapes.rounded,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) = SurfaceContainer(
    modifier = modifier,
    shape = shape,
    contentAlignment = contentAlignment,
    content = content,
)

@Composable
fun Surface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role? = null,
    shape: Shape = Theme.shapes.rounded,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) = SurfaceContainer(
    modifier = modifier,
    shape = shape,
    contentAlignment = contentAlignment,
    enabled = enabled,
    onClick = onClick,
    role = role,
    content = content,
)

@Composable
internal fun SurfaceContainer(
    modifier: Modifier,
    shape: Shape,
    contentAlignment: Alignment,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    role: Role? = null,
    reactsToHover: Boolean = false,
    borderColor: Color? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val currentLevel = LocalElevationLevel.current
    val newLevel = currentLevel + 1

    val elevation = LocalElevation.current
    val backgroundColor = elevation.backgroundColor(if (isPressed) currentLevel else newLevel)
    val resolvedBorderColor = borderColor ?: elevation.borderColor(if (enabled && isHovered) newLevel + 1 else newLevel)

    val interactions = when {
        onClick != null -> Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = role,
                onClick = onClick
            )
            .then(if (enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)

        reactsToHover -> Modifier.hoverable(interactionSource = interactionSource, enabled = enabled)
        else -> Modifier
    }

    CompositionLocalProvider(
        LocalElevationLevel provides newLevel,
        LocalSurfaceBorderColor provides resolvedBorderColor,
    ) {
        Box(
            modifier = modifier
                .background(backgroundColor, shape)
                .border(Theme.borderWidth, resolvedBorderColor, shape)
                .then(interactions),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}
