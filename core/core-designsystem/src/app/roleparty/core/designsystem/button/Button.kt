package app.roleparty.core.designsystem.button

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabled

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: IconVector? = null,
    trailingIcon: IconVector? = null,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.NORMAL,
    shape: Shape = Theme.shapes.rounded,
) {
    val tint = iconTint(enabled)

    Surface(
        onClick = onClick,
        modifier = modifier.height(size.height),
        enabled = enabled,
        role = Role.Button,
        shape = shape,
        contentAlignment = Alignment.Center,
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
            icon?.let {
                Icon(vector = it, tint = tint, size = size.iconSize)
                Spacer(Modifier.width(size.leadingSpacing))
            }

            Text(
                text = text,
                color = if (enabled) Theme.colors.textSecondary else Theme.colors.textSecondary.disabled()
            )

            trailingIcon?.let {
                Spacer(Modifier.width(size.indicatorSpacing))
                Icon(vector = it, tint = tint, size = size.indicatorIconSize)
            }
        }
    }
}

@Composable
fun IconButton(
    icon: IconVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.NORMAL,
    shape: Shape = Theme.shapes.rounded,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size.height),
        enabled = enabled,
        role = Role.Button,
        shape = shape,
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            vector = icon,
            tint = iconTint(enabled),
            contentDescription = contentDescription,
            size = size.iconSize,
        )
    }
}

@Composable
private fun iconTint(enabled: Boolean): Color =
    if (enabled) Theme.colors.primary else Theme.colors.textSecondary.disabled()
