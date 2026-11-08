package app.roleparty.campaign.library.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.library.frontend.component.resources.Res
import app.roleparty.campaign.library.frontend.component.resources.add_entry
import app.roleparty.campaign.library.frontend.component.resources.cancel
import app.roleparty.campaign.library.frontend.component.resources.creating
import app.roleparty.campaign.library.frontend.component.resources.entry_description
import app.roleparty.campaign.library.frontend.component.resources.entry_image
import app.roleparty.campaign.library.frontend.component.resources.entry_title
import app.roleparty.campaign.library.frontend.component.resources.no_entries
import app.roleparty.campaign.library.frontend.component.resources.select_entry
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.dialog.Dialog
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.CenteredBox
import app.roleparty.core.designsystem.layout.DetailPane
import app.roleparty.core.designsystem.layout.ListDetailLayout
import app.roleparty.core.designsystem.layout.ListItem
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageThumbnail
import app.roleparty.media.frontend.component.RemoteImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun Library(
    entries: List<LibraryEntryResponseBody>,
    selected: LibraryEntryResponseBody?,
    error: String?,
    imageUrl: (ImageId) -> String,
    onSelect: (LibraryEntryId) -> Unit,
    onCreate: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListDetailLayout(
        modifier = modifier,
        listToolbar = { Button(onClick = onCreate, leadingIcon = IconVector.ADD) },
        list = {
            if (entries.isEmpty()) EmptyState(stringResource(Res.string.no_entries))
            entries.forEach { entry ->
                ListItem(
                    selected = entry.id == selected?.id,
                    onClick = { onSelect(entry.id) },
                    thumbnail = { ImageThumbnail(url = imageUrl(entry.imageId), contentDescription = entry.title) },
                ) {
                    Text(text = entry.title, style = Theme.typography.title, maxLines = 1)
                }
            }
        },
        detail = {
            if (selected == null) {
                CenteredBox { Text(stringResource(Res.string.select_entry)) }
            } else {
                DetailPane {
                    ErrorCallout(error, Modifier.fillMaxWidth())
                    RemoteImage(url = imageUrl(selected.imageId), contentDescription = selected.title, modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp))
                    StringField(
                        value = selected.title,
                        onValueChange = onTitleChange,
                        label = stringResource(Res.string.entry_title),
                        required = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    StringField(
                        value = selected.description,
                        onValueChange = onDescriptionChange,
                        label = stringResource(Res.string.entry_description),
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
    )
}

@Composable
fun LibraryEntryCreationDialog(
    displayed: Boolean,
    title: String,
    description: String,
    submitting: Boolean,
    canSubmit: Boolean,
    error: String?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    imageField: @Composable (label: String, modifier: Modifier) -> Unit,
) {
    Dialog(displayed = displayed, onDismiss = onDismiss, modifier = Modifier.width(560.dp)) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = stringResource(Res.string.add_entry), style = Theme.typography.headline)
            imageField(stringResource(Res.string.entry_image), Modifier.fillMaxWidth())
            StringField(
                value = title,
                onValueChange = onTitleChange,
                label = stringResource(Res.string.entry_title),
                enabled = !submitting,
                required = true,
                modifier = Modifier.fillMaxWidth(),
            )
            StringField(
                value = description,
                onValueChange = onDescriptionChange,
                label = stringResource(Res.string.entry_description),
                enabled = !submitting,
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
            ErrorCallout(error, Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(text = stringResource(Res.string.cancel), onClick = onDismiss, leadingIcon = IconVector.CANCEL, enabled = !submitting)
                Button(
                    text = stringResource(if (submitting) Res.string.creating else Res.string.add_entry),
                    onClick = onSubmit,
                    leadingIcon = IconVector.ADD,
                    enabled = canSubmit && !submitting,
                )
            }
        }
    }
}
