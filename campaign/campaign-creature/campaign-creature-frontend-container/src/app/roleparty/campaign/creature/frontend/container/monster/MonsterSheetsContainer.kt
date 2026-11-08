package app.roleparty.campaign.creature.frontend.container.monster

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.component.MonsterSheet
import app.roleparty.campaign.creature.frontend.component.MonsterSheets
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.imagefield.ImageFieldContainer
import org.koin.compose.koinInject

class MonsterSheetsContainer(campaignId: CampaignId) :
    Container<MonsterSheetsState, MonsterSheetsAction, MonsterSheetsEvent, MonsterSheetsViewModel>(MonsterSheetsViewModel::class, campaignId) {

    @Composable
    override fun Content(state: MonsterSheetsState, handle: (MonsterSheetsAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            MonsterSheetsState.Loading -> FullScreenLoader()
            is MonsterSheetsState.Failed -> RetryableFailure(state.failure) { handle(MonsterSheetsAction.Retry) }
            is MonsterSheetsState.Loaded -> MonsterSheets(
                overviews = state.overviews,
                selectedId = state.editor?.monsterSheetId,
                error = state.failure?.message(),
                imageUrl = media::imageUrl,
                onSelect = { handle(MonsterSheetsAction.Select(it)) },
                onCreate = { handle(MonsterSheetsAction.Create) },
            ) {
                val editor = state.editor
                val sheet = editor?.sheet
                if (editor == null || sheet == null) {
                    FullScreenLoader()
                } else {
                    MonsterSheet(
                        sheet = sheet,
                        editing = editor.editing,
                        saving = editor.saving,
                        error = editor.failure?.message(),
                        imageUrl = media::imageUrl,
                        onEdit = { handle(MonsterSheetsAction.StartEditing) },
                        onDuplicate = { handle(MonsterSheetsAction.Duplicate) },
                        onCancel = { handle(MonsterSheetsAction.CancelEditing) },
                        onSave = { handle(MonsterSheetsAction.Save) },
                        onChange = { handle(MonsterSheetsAction.Change(it)) },
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
        }
    }
}
