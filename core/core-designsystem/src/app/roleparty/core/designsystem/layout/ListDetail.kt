package app.roleparty.core.designsystem.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.divider.HorizontalDivider
import app.roleparty.core.designsystem.elevation.LocalElevation
import app.roleparty.core.designsystem.elevation.LocalElevationLevel
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.theme.Theme

@Composable
fun ListDetailLayout(
    list: @Composable ColumnScope.() -> Unit,
    detail: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    listToolbar: (@Composable RowScope.() -> Unit)? = null,
    listWidth: Dp = 320.dp,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = modifier.fillMaxSize()) {
            Column(Modifier.width(listWidth)) {
                listToolbar?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = it,
                    )
                    HorizontalDivider()
                }
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    content = list,
                )
            }
            Surface(modifier = Modifier.weight(1f).fillMaxHeight(), shape = Theme.shapes.rectangle, content = detail)
        }
    }
}

@Composable
fun ListItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    thumbnail: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val background =
        if (selected || hovered) LocalElevation.current.backgroundColor(LocalElevationLevel.current + 1) else Color.Transparent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.rectangle)
            .background(background)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .height(80.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
        thumbnail?.let { Box(modifier = Modifier.size(ThumbnailSize), contentAlignment = Alignment.Center, content = it) }
    }
}

@Composable
fun DetailPane(
    modifier: Modifier = Modifier,
    toolbar: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxSize()) {
        toolbar?.let {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = it,
            )
            HorizontalDivider()
        }
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, actions: (@Composable RowScope.() -> Unit)? = null) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            app.roleparty.core.designsystem.text.Text(
                text = title,
                style = Theme.typography.title,
                modifier = Modifier.weight(1f)
            )
            actions?.invoke(this)
        }
        HorizontalDivider()
    }
}

@Composable
fun CenteredBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = content)
}
