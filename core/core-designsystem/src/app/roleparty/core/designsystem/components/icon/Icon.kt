package app.roleparty.core.designsystem.components.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.vectorResource

@Composable
fun Icon(
    vector: IconVector,
    contentDescription: String? = null,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
) = Image(
    imageVector = vectorResource(vector.drawableResource),
    contentDescription = contentDescription,
    modifier = modifier.size(size),
    colorFilter = ColorFilter.tint(tint)
)