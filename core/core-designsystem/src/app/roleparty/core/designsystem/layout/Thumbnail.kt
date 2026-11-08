package app.roleparty.core.designsystem.layout

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.theme.Theme

val ThumbnailSize = DpSize(70.dp, 56.dp)

@Composable
fun Thumbnail(
    modifier: Modifier = Modifier,
    shape: Shape = Theme.shapes.roundedSmall,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Surface(modifier = modifier.fillMaxSize().clip(shape), shape = shape, contentAlignment = contentAlignment, content = content)
}
