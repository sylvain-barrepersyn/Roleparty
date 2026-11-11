package app.roleparty.campaign.creature.frontend.container.creatures

import androidx.compose.runtime.Composable
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.frontend.component.CreatureSelection
import app.roleparty.campaign.creature.frontend.component.Creatures
import app.roleparty.campaign.creature.frontend.container.character.CharacterSheetContainer
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetContainer
import app.roleparty.core.designsystem.feedback.FullScreenLoader
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class CreaturesContainer(private val campaignId: CampaignId) :
    Container<CreaturesState, CreaturesAction, CreaturesEvent, CreaturesViewModel>(CreaturesViewModel::class, campaignId) {

    @Composable
    override fun Content(state: CreaturesState, handle: (CreaturesAction) -> Unit) {
        val media = koinInject<MediaApi>()

        when (state) {
            CreaturesState.Loading -> FullScreenLoader()
            is CreaturesState.Failed -> RetryableFailure(state.failure) { handle(CreaturesAction.Retry) }
            is CreaturesState.Loaded -> Creatures(
                monsters = state.monsters,
                characters = state.characters,
                selected = state.selected,
                error = state.failure?.message(),
                imageUrl = media::imageUrl,
                onSelect = { handle(CreaturesAction.Select(it)) },
                onCreateMonster = { handle(CreaturesAction.CreateMonster) },
            ) {
                when (val selected = state.selected) {
                    null -> Unit
                    is CreatureSelection.Monster -> MonsterSheetContainer(
                        campaignId = campaignId,
                        monsterSheetId = selected.monsterSheetId,
                        onSaved = { handle(CreaturesAction.Refresh()) },
                        onCreated = { handle(CreaturesAction.Refresh(select = it)) },
                    ).Render()
                    is CreatureSelection.Character -> CharacterSheetContainer(
                        campaignId = campaignId,
                        characterSheetId = selected.characterSheetId,
                        onSaved = { handle(CreaturesAction.Refresh()) },
                    ).Render()
                }
            }
        }
    }
}
