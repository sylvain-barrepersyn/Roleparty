package app.roleparty.campaign.frontend.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.frontend.component.RemoteImage

@Composable
fun CampaignImage(url: String, contentDescription: String?, modifier: Modifier = Modifier, size: Dp = 56.dp) {
    RemoteImage(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier.width(size).aspectRatio(80f / 64f).clip(Theme.shapes.roundedSmall),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ErrorCallout(error: String?, modifier: Modifier = Modifier) {
    error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = modifier) }
}
