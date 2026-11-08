package app.roleparty.campaign.story.frontend.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.frontend.component.RemoteImage

@Composable
internal fun ErrorCallout(error: String?, modifier: Modifier = Modifier) {
    error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = modifier) }
}

@Composable
internal fun EntryImage(url: String, contentDescription: String?, size: Dp) {
    RemoteImage(
        url = url,
        contentDescription = contentDescription,
        modifier = Modifier.width(size).aspectRatio(80f / 64f).clip(Theme.shapes.rounded),
        contentScale = ContentScale.Crop,
    )
}
