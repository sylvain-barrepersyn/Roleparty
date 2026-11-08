package app.roleparty.core.designsystem.button

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.icon.iconTint
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.core.designsystem.theme.disabledIf

@Composable
fun Button(
    leadingIcon: IconVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    trailingIcon: IconVector? = null,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.NORMAL,
    shape: Shape = Theme.shapes.roundedSmall,
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
                .padding(horizontal = size.horizontalPadding)
                .animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    ),
                    alignment = Alignment.CenterStart
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(vector = leadingIcon, tint = tint, size = size.leadingIconSize)

            text?.let {
                Spacer(Modifier.width(size.leadingSpacing))
                Text(text = it, color = Theme.colors.textSecondary.disabledIf(!enabled))
            }

            trailingIcon?.let {
                Spacer(Modifier.width(size.trailingSpacing))
                Icon(vector = it, tint = tint, size = size.trailingIconSize)
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
    shape: Shape = Theme.shapes.roundedSmall,
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
            size = size.leadingIconSize,
        )
    }
}
