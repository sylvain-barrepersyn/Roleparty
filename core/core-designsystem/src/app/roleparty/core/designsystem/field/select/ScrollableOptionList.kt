package app.roleparty.core.designsystem.field.select

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun ScrollableOptionList(
    modifier: Modifier = Modifier,
    maxHeight: Dp = 240.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val fadeColor = Theme.colors.textSecondary.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .padding(start = Theme.borderWidth, end = Theme.borderWidth, bottom = Theme.borderWidth)
            .clip(Theme.shapes.rounded)
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            content = content,
        )

        if (scrollState.canScrollBackward) {
            ScrollFade(Alignment.TopCenter, listOf(fadeColor, Color.Transparent))
        }

        if (scrollState.canScrollForward) {
            ScrollFade(Alignment.BottomCenter, listOf(Color.Transparent, fadeColor))
        }
    }
}

@Composable
private fun BoxScope.ScrollFade(alignment: Alignment, colors: List<Color>) {
    Box(
        modifier = Modifier
            .align(alignment)
            .fillMaxWidth()
            .height(8.dp)
            .background(Brush.verticalGradient(colors = colors))
    )
}
