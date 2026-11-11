package app.roleparty.campaign.creature.frontend.container.monster

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.frontend.component.CreatureSelection
import app.roleparty.campaign.creature.frontend.component.CreatureSheet
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.imagefield.ImageFieldContainer
import org.koin.compose.koinInject

class MonsterSheetContainer(
    campaignId: CampaignId,
    monsterSheetId: MonsterSheetId,
    private val onSaved: () -> Unit,
    private val onCreated: (CreatureSelection) -> Unit,
) : Container<MonsterSheetState, MonsterSheetAction, MonsterSheetEvent, MonsterSheetViewModel>(MonsterSheetViewModel::class, campaignId, monsterSheetId) {

    @Composable
    override fun Content(state: MonsterSheetState, handle: (MonsterSheetAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            MonsterSheetState.Loading -> FullScreenLoader()
            is MonsterSheetState.Failed -> RetryableFailure(state.failure) { handle(MonsterSheetAction.Retry) }
            is MonsterSheetState.Loaded -> CreatureSheet(
                sheet = state.sheet,
                editing = state.editing,
                saving = state.saving,
                error = state.failure?.message(),
                imageUrl = media::imageUrl,
                onEdit = { handle(MonsterSheetAction.StartEditing) },
                onCancel = { handle(MonsterSheetAction.CancelEditing) },
                onSave = { handle(MonsterSheetAction.Save) },
                onChange = { handle(MonsterSheetAction.Change(it)) },
                onDuplicate = { handle(MonsterSheetAction.Duplicate) },
                onCreateCharacter = { handle(MonsterSheetAction.CreateCharacter) },
                        imageField = { label, value, onValueChange, modifier ->
                            ImageFieldContainer(
                                fieldId = "monster-sheet",
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

    override fun onEvent(event: MonsterSheetEvent) = when (event) {
        MonsterSheetEvent.Saved -> onSaved()
        is MonsterSheetEvent.Duplicated -> onCreated(CreatureSelection.Monster(event.monsterSheetId))
        is MonsterSheetEvent.CharacterCreated -> onCreated(CreatureSelection.Character(event.characterSheetId))
    }
}
