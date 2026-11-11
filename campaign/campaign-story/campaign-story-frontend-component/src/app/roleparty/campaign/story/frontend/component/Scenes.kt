package app.roleparty.campaign.story.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.contract.web.SceneResponseBody
import app.roleparty.campaign.story.frontend.component.resources.Res
import app.roleparty.campaign.story.frontend.component.resources.cancel
import app.roleparty.campaign.story.frontend.component.resources.delete_scene
import app.roleparty.campaign.story.frontend.component.resources.delete_scene_confirmation
import app.roleparty.campaign.story.frontend.component.resources.link_entry
import app.roleparty.campaign.story.frontend.component.resources.no_linked_entries
import app.roleparty.campaign.story.frontend.component.resources.new_scene
import app.roleparty.campaign.story.frontend.component.resources.no_scenes
import app.roleparty.campaign.story.frontend.component.resources.scene_description
import app.roleparty.campaign.story.frontend.component.resources.scene_library
import app.roleparty.campaign.story.frontend.component.resources.scene_name
import app.roleparty.campaign.story.frontend.component.resources.select_scene
import app.roleparty.campaign.story.frontend.component.resources.unlink_entry
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.dialog.Dialog
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.field.text.ParagraphsField
import app.roleparty.core.document.Document
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun Scenes(
    scenes: List<SceneResponseBody>,
    selected: SceneResponseBody?,
    libraryEntries: List<LibraryEntryResponseBody>,
    error: String?,
    imageUrl: (ImageId) -> String,
    onSelect: (SceneId) -> Unit,
    onCreate: (defaultName: String) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (Document) -> Unit,
    onDelete: () -> Unit,
    onLinkLibraryEntry: (LibraryEntryId) -> Unit,
    onUnlinkLibraryEntry: (LibraryEntryId) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListDetailLayout(
        modifier = modifier,
        listToolbar = { val defaultName = stringResource(Res.string.new_scene)
            Button(onClick = { onCreate(defaultName) }, leadingIcon = IconVector.ADD) },
        list = {
            if (scenes.isEmpty()) EmptyState(stringResource(Res.string.no_scenes))
            scenes.forEach { scene ->
                ListItem(
                    selected = scene.id == selected?.id,
                    onClick = { onSelect(scene.id) },
                    thumbnail = {
                        ImageMosaicThumbnail(
                            scene.libraryEntryIds.mapNotNull { id -> libraryEntries.firstOrNull { it.id == id } }
                                .map { ThumbnailImage(imageUrl(it.imageId), it.title) }
                        )
                    },
                ) {
                    Text(text = scene.name, style = Theme.typography.title, maxLines = 1)
                }
            }
        },
        detail = {
            if (selected == null) {
                CenteredBox { Text(stringResource(Res.string.select_scene)) }
            } else {
                SceneDetail(
                    scene = selected,
                    libraryEntries = libraryEntries,
                    error = error,
                    imageUrl = imageUrl,
                    onNameChange = onNameChange,
                    onDescriptionChange = onDescriptionChange,
                    onDelete = onDelete,
                    onLinkLibraryEntry = onLinkLibraryEntry,
                    onUnlinkLibraryEntry = onUnlinkLibraryEntry,
                )
            }
        },
    )
}

@Composable
private fun SceneDetail(
    scene: SceneResponseBody,
    libraryEntries: List<LibraryEntryResponseBody>,
    error: String?,
    imageUrl: (ImageId) -> String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (Document) -> Unit,
    onDelete: () -> Unit,
    onLinkLibraryEntry: (LibraryEntryId) -> Unit,
    onUnlinkLibraryEntry: (LibraryEntryId) -> Unit,
) {
    var confirmingDeletion by remember(scene.id) { mutableStateOf(false) }
    val linked = scene.libraryEntryIds.mapNotNull { id -> libraryEntries.firstOrNull { it.id == id } }
    val linkable = libraryEntries.filterNot { it.id in scene.libraryEntryIds }

    DetailPane(
        toolbar = {
            Spacer(Modifier.weight(1f))
            Button(text = stringResource(Res.string.delete_scene), onClick = { confirmingDeletion = true }, leadingIcon = IconVector.DELETE)
        },
    ) {
        ErrorCallout(error, Modifier.fillMaxWidth())
        StringField(
            value = scene.name,
            onValueChange = onNameChange,
            label = stringResource(Res.string.scene_name),
            required = true,
            modifier = Modifier.fillMaxWidth(),
        )
        ParagraphsField(
            document = scene.description,
            onDocumentChange = onDescriptionChange,
            structured = true,
            modifier = Modifier.fillMaxWidth(),
        )
        SectionHeader(title = stringResource(Res.string.scene_library))
        if (linked.isEmpty()) EmptyState(stringResource(Res.string.no_linked_entries))
        linked.forEach { entry ->
            LibraryEntryRow(entry, imageUrl) {
                Button(text = stringResource(Res.string.unlink_entry), onClick = { onUnlinkLibraryEntry(entry.id) }, leadingIcon = IconVector.CANCEL, size = ButtonSize.SMALL)
            }
        }
        linkable.forEach { entry ->
            LibraryEntryRow(entry, imageUrl) {
                Button(text = stringResource(Res.string.link_entry), onClick = { onLinkLibraryEntry(entry.id) }, leadingIcon = IconVector.ADD, size = ButtonSize.SMALL)
            }
        }
    }

    Dialog(displayed = confirmingDeletion, onDismiss = { confirmingDeletion = false }, modifier = Modifier.width(480.dp)) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = stringResource(Res.string.delete_scene_confirmation, scene.name), color = Theme.colors.textPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(text = stringResource(Res.string.cancel), onClick = { confirmingDeletion = false }, leadingIcon = IconVector.CANCEL)
                Button(
                    text = stringResource(Res.string.delete_scene),
                    onClick = {
                        confirmingDeletion = false
                        onDelete()
                    },
                    leadingIcon = IconVector.DELETE,
                )
            }
        }
    }
}

@Composable
private fun LibraryEntryRow(entry: LibraryEntryResponseBody, imageUrl: (ImageId) -> String, action: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        EntryImage(url = imageUrl(entry.imageId), contentDescription = entry.title, size = 40.dp)
        Text(text = entry.title, color = Theme.colors.textPrimary, modifier = Modifier.weight(1f))
        action()
    }
}
