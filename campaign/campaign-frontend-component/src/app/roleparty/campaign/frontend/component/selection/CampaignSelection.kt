package app.roleparty.campaign.frontend.component.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.component.CampaignImage
import app.roleparty.campaign.frontend.component.ErrorCallout
import app.roleparty.campaign.frontend.component.resources.Res
import app.roleparty.campaign.frontend.component.resources.create_campaign
import app.roleparty.campaign.frontend.component.resources.game_master
import app.roleparty.campaign.frontend.component.resources.join_campaign
import app.roleparty.campaign.frontend.component.resources.no_mastered_campaigns
import app.roleparty.campaign.frontend.component.resources.no_played_campaigns
import app.roleparty.campaign.frontend.component.resources.player
import app.roleparty.campaign.frontend.component.resources.players
import app.roleparty.campaign.frontend.component.resources.refresh
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.feedback.Loader
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.tab.Tab
import app.roleparty.core.designsystem.tab.TabGroup
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CampaignSelection(
    participation: Participation,
    campaigns: List<CampaignOverviewResponseBody>?,
    error: String?,
    imageUrl: (ImageId) -> String,
    onParticipationChange: (Participation) -> Unit,
    onRefresh: () -> Unit,
    onSelect: ((CampaignId) -> Unit)?,
    onCreate: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier,
    footer: @Composable RowScope.() -> Unit = {},
) {
    Column(modifier = modifier.widthIn(max = 700.dp).padding(64.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TabGroup(modifier = Modifier.weight(1f)) {
                Tab(
                    label = stringResource(Res.string.game_master),
                    selected = participation == Participation.GAME_MASTER,
                    onClick = { onParticipationChange(Participation.GAME_MASTER) },
                )
                Tab(
                    label = stringResource(Res.string.player),
                    selected = participation == Participation.PLAYER,
                    onClick = { onParticipationChange(Participation.PLAYER) },
                )
            }
            if (campaigns == null && error == null) {
                Loader()
            } else {
                IconButton(icon = IconVector.REFRESH, contentDescription = stringResource(Res.string.refresh), onClick = onRefresh)
            }
        }

        ErrorCallout(error, Modifier.fillMaxWidth())

        when {
            campaigns == null -> Unit
            campaigns.isEmpty() -> EmptyState(
                text = stringResource(if (participation == Participation.GAME_MASTER) Res.string.no_mastered_campaigns else Res.string.no_played_campaigns),
                modifier = Modifier.fillMaxWidth(),
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                campaigns.forEach { campaign ->
                    CampaignCard(campaign = campaign, imageUrl = imageUrl(campaign.imageId), onClick = onSelect?.let { { it(campaign.id) } })
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            footer()
            Spacer(Modifier.weight(1f))
            when (participation) {
                Participation.GAME_MASTER -> Button(text = stringResource(Res.string.create_campaign), onClick = onCreate, leadingIcon = IconVector.ADD)
                Participation.PLAYER -> Button(text = stringResource(Res.string.join_campaign), onClick = onJoin, leadingIcon = IconVector.ADD)
            }
        }
    }
}

@Composable
private fun CampaignCard(campaign: CampaignOverviewResponseBody, imageUrl: String, onClick: (() -> Unit)?) {
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CampaignImage(url = imageUrl, contentDescription = campaign.name, size = 70.dp)
            Column(Modifier.weight(1f)) {
                Text(text = campaign.name, style = Theme.typography.title)
                Text(text = pluralStringResource(Res.plurals.players, campaign.players, campaign.players), style = Theme.typography.subtitle)
            }
        }
    }

    if (onClick != null) {
        Surface(onClick = onClick, modifier = Modifier.fillMaxWidth()) { content() }
    } else {
        Surface(modifier = Modifier.fillMaxWidth()) { content() }
    }
}
