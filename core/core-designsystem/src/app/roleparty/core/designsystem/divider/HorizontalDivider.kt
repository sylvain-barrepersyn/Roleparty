package app.roleparty.core.designsystem.divider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import app.roleparty.core.designsystem.surface.LocalSurfaceBorderColor
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalSurfaceBorderColor.current,
    thickness: Dp = Theme.borderWidth,
) = Spacer(
    modifier = modifier
        .fillMaxWidth()
        .height(thickness)
        .background(color)
)
