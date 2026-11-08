package app.roleparty.core.designsystem.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.designsystem.resources.Res
import app.roleparty.designsystem.resources.loading
import org.jetbrains.compose.resources.stringResource

private const val TURN_MILLIS = 1000

@Composable
fun Loader(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(TURN_MILLIS, easing = LinearEasing), RepeatMode.Restart),
    )

    Icon(
        vector = IconVector.LOADER,
        tint = Theme.colors.primary,
        contentDescription = stringResource(Res.string.loading),
        modifier = modifier.rotate(rotation),
        size = size,
    )
}

@Composable
fun FullScreenLoader(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Loader(size = 48.dp)
    }
}
