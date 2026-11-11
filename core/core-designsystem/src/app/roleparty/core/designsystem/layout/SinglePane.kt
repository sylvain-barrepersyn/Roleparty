package app.roleparty.core.designsystem.layout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.divider.HorizontalDivider
import app.roleparty.core.designsystem.surface.Surface

@Composable
fun SinglePaneLayout(
    modifier: Modifier = Modifier,
    toolbar: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            toolbar?.let {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = it,
                )
                HorizontalDivider()
            }
            Box(modifier = Modifier.padding(8.dp).weight(1f).fillMaxWidth(), content = content)
        }
    }
}
