package app.roleparty.media.frontend.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.Thumbnail
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun ImageThumbnail(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.shapes.roundedSmall,
    placeholder: IconVector? = null,
) {
    Thumbnail(modifier = modifier, shape = shape) {
        if (url != null) {
            RemoteImage(url = url, contentDescription = contentDescription, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else if (placeholder != null) {
            Icon(vector = placeholder, tint = Theme.colors.textSecondary)
        }
    }
}
