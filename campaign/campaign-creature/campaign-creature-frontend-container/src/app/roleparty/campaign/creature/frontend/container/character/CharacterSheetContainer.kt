package app.roleparty.campaign.creature.frontend.container.character

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.frontend.component.CreatureSheet
import app.roleparty.campaign.creature.frontend.component.InheritedMonster
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.imagefield.ImageFieldContainer
import org.koin.compose.koinInject

class CharacterSheetContainer(
    campaignId: CampaignId,
    characterSheetId: CharacterSheetId,
    private val onSaved: () -> Unit,
) : Container<CharacterSheetState, CharacterSheetAction, CharacterSheetEvent, CharacterSheetViewModel>(CharacterSheetViewModel::class, campaignId, characterSheetId) {

    @Composable
    override fun Content(state: CharacterSheetState, handle: (CharacterSheetAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            CharacterSheetState.Loading -> FullScreenLoader()
            is CharacterSheetState.Failed -> RetryableFailure(state.failure) { handle(CharacterSheetAction.Retry) }
            is CharacterSheetState.Loaded -> CreatureSheet(
                sheet = state.character.sheet,
                editing = state.editing,
                saving = state.saving,
                error = state.failure?.message(),
                imageUrl = media::imageUrl,
                onEdit = { handle(CharacterSheetAction.StartEditing) },
                onCancel = { handle(CharacterSheetAction.CancelEditing) },
                onSave = { handle(CharacterSheetAction.Save) },
                onChange = { handle(CharacterSheetAction.Change(it)) },
                notes = state.character.notes,
                onNotesChange = { handle(CharacterSheetAction.ChangeNotes(it)) },
                inheritedFrom = state.monster?.let { InheritedMonster(it.name, it.imageId?.let(media::imageUrl)) },
                onUnlink = { handle(CharacterSheetAction.Unlink) },
                        imageField = { label, value, onValueChange, modifier ->
                            ImageFieldContainer(
                                fieldId = "character-sheet",
                                label = label,
                                value = value,
                                onValueChange = onValueChange,
                                modifier = modifier,
                                imageSize = DpSize(300.dp, 240.dp),
                            ).Render()
                        },
            )
        }
    }

    override fun onEvent(event: CharacterSheetEvent) = when (event) {
        CharacterSheetEvent.Saved -> onSaved()
    }
}
