package app.roleparty.core.designsystem.components.button

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.components.icon.Icon
import app.roleparty.core.designsystem.components.icon.IconVector
import app.roleparty.core.designsystem.components.surface.Surface
import app.roleparty.core.designsystem.components.text.BodyText
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun Button(
    icon: IconVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.NORMAL,
    shape: ButtonShape = ButtonShape.ROUNDED,
    trailingIcon: IconVector? = null,
    text: String? = null
) {
    require(contentDescription != null || text != null) { "Either contentDescription or text must be provided" }

    val interactionSource = remember { MutableInteractionSource() }

    val contentColor by rememberUpdatedState(
        if (enabled) Theme.colors.textSecondary else Theme.colors.textSecondary.copy(alpha = 0.5f)
    )

    val iconColor by rememberUpdatedState(
        if (enabled) Theme.colors.primary else Theme.colors.textSecondary.copy(alpha = 0.5f)
    )

    val resolvedShape = when (shape) {
        ButtonShape.CIRCLE -> CircleShape
        ButtonShape.ROUNDED -> RoundedCornerShape(8.dp)
    }

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = resolvedShape,
        modifier = modifier.height(size.height),
        enabled = enabled,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = size.horizontalPadding, vertical = size.verticalPadding)
                .animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    ),
                    alignment = Alignment.CenterStart
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(size.indicatorSpacing)
            ) {
                val leadingIconDescription = if (text == null) contentDescription else null

                Icon(
                    vector = icon,
                    contentDescription = leadingIconDescription,
                    tint = iconColor
                )

                text?.let { BodyText(text = it, color = contentColor) }

                trailingIcon?.let {
                    Icon(
                        vector = it,
                        contentDescription = null,
                        tint = iconColor
                    )
                }
            }
        }
    }
}
