package app.roleparty.media.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.layout.Thumbnail
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme

data class ThumbnailImage(val url: String, val contentDescription: String?)

private val GAP = 2.dp
private const val CELLS = 4

@Composable
fun ImageMosaicThumbnail(images: List<ThumbnailImage>, modifier: Modifier = Modifier) {
    if (images.size == 1) {
        val image = images.single()
        ImageThumbnail(url = image.url, contentDescription = image.contentDescription, modifier = modifier)
        return
    }

    val overflowing = images.size > CELLS
    val shown = if (overflowing) images.take(CELLS - 1) else images
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(GAP)) {
        repeat(2) { row ->
            Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(GAP)) {
                repeat(2) { column ->
                    val index = row * 2 + column
                    val cell = Modifier.weight(1f).fillMaxHeight()
                    when {
                        index < shown.size -> ImageThumbnail(
                            url = shown[index].url,
                            contentDescription = shown[index].contentDescription,
                            modifier = cell,
                            shape = Theme.shapes.roundedSmall,
                        )
                        overflowing -> Thumbnail(modifier = cell, shape = Theme.shapes.roundedSmall) {
                            Text(text = "…", style = Theme.typography.bodySmall, maxLines = 1)
                        }
                        else -> Thumbnail(modifier = cell, shape = Theme.shapes.roundedSmall)
                    }
                }
            }
        }
    }
}
