package app.roleparty.campaign.library.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.contract.web.JournalEntryResponseBody
import app.roleparty.campaign.library.frontend.component.resources.Res
import app.roleparty.campaign.library.frontend.component.resources.add_image
import app.roleparty.campaign.library.frontend.component.resources.journal_entry_images
import app.roleparty.campaign.library.frontend.component.resources.journal_entry_text
import app.roleparty.campaign.library.frontend.component.resources.journal_entry_title
import app.roleparty.campaign.library.frontend.component.resources.no_journal_entries
import app.roleparty.campaign.library.frontend.component.resources.remove_image
import app.roleparty.campaign.library.frontend.component.resources.select_journal_entry
import app.roleparty.campaign.library.frontend.component.resources.untitled_journal_entry
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.field.text.RichTextField
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.CenteredBox
import app.roleparty.core.designsystem.layout.DetailPane
import app.roleparty.core.designsystem.layout.ListDetailLayout
import app.roleparty.core.designsystem.layout.ListItem
import app.roleparty.core.designsystem.layout.SectionHeader
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageMosaicThumbnail
import app.roleparty.media.frontend.component.ThumbnailImage
import app.roleparty.media.frontend.component.RemoteImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun Journal(
    entries: List<JournalEntryResponseBody>,
    selected: JournalEntryResponseBody?,
    creating: Boolean,
    error: String?,
    imageUrl: (ImageId) -> String,
    onCreate: () -> Unit,
    onSelect: (JournalEntryId) -> Unit,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onRemoveImage: (ImageId) -> Unit,
    modifier: Modifier = Modifier,
    imageField: @Composable (label: String, modifier: Modifier) -> Unit,
) {
    ListDetailLayout(
        modifier = modifier,
        listToolbar = { Button(onClick = onCreate, leadingIcon = IconVector.ADD, enabled = !creating) },
        list = {
            if (entries.isEmpty()) EmptyState(stringResource(Res.string.no_journal_entries))
            entries.forEach { entry ->
                ListItem(
                    selected = entry.id == selected?.id,
                    onClick = { onSelect(entry.id) },
                    thumbnail = { ImageMosaicThumbnail(entry.images.map { ThumbnailImage(imageUrl(it), entry.title) }) },
                ) {
                    Text(
                        text = entry.title.ifBlank { stringResource(Res.string.untitled_journal_entry) },
                        style = Theme.typography.title,
                        color = if (entry.title.isBlank()) Theme.colors.textSecondary else Theme.colors.textPrimary,
                        maxLines = 1,
                    )
                }
            }
        },
        detail = {
            if (selected == null) {
                CenteredBox { Text(stringResource(Res.string.select_journal_entry)) }
            } else key(selected.id) {
                DetailPane {
                    ErrorCallout(error, Modifier.fillMaxWidth())
                    StringField(
                        value = selected.title,
                        onValueChange = onTitleChange,
                        label = stringResource(Res.string.journal_entry_title),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    RichTextField(
                        markdown = selected.text,
                        onMarkdownChange = onTextChange,
                        label = stringResource(Res.string.journal_entry_text),
                        modifier = Modifier.fillMaxWidth(),
                        minHeight = 240.dp,
                        structured = true,
                    )
                    SectionHeader(stringResource(Res.string.journal_entry_images))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selected.images.forEach { image ->
                            JournalImage(imageUrl(image), onRemove = { onRemoveImage(image) })
                        }
                    }
                    imageField(stringResource(Res.string.add_image), Modifier.fillMaxWidth())
                }
            }
        },
    )
}

@Composable
private fun JournalImage(url: String, onRemove: () -> Unit) {
    Box(Modifier.size(width = 240.dp, height = 160.dp)) {
        RemoteImage(url = url, contentDescription = null, modifier = Modifier.size(width = 240.dp, height = 160.dp).clip(Theme.shapes.roundedSmall), contentScale = ContentScale.Crop)
        IconButton(
            icon = IconVector.DELETE,
            contentDescription = stringResource(Res.string.remove_image),
            onClick = onRemove,
            size = ButtonSize.TINY,
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
        )
    }
}
