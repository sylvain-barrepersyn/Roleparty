package app.roleparty.campaign.creature.frontend.component.playercharacter

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind

@Composable
internal fun ErrorCallout(error: String?, modifier: Modifier = Modifier) {
    error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = modifier) }
}
