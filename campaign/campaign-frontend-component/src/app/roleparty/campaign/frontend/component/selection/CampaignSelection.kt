package app.roleparty.campaign.frontend.component.selection

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.frontend.component.CampaignImage
import app.roleparty.campaign.frontend.component.ErrorCallout
import app.roleparty.campaign.frontend.component.resources.*
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.feedback.Loader
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.Thumbnail
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.tab.Tab
import app.roleparty.core.designsystem.tab.TabGroup
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.time.format.TextStyle
import java.util.*

@Composable
fun CampaignSelection(
    participation: Participation,
    campaigns: List<CampaignOverviewResponseBody>?,
    today: LocalDate?,
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
    Column(
        modifier = modifier.widthIn(max = 700.dp).padding(64.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Loader()
                }
            } else {
                IconButton(
                    icon = IconVector.REFRESH,
                    contentDescription = stringResource(Res.string.refresh),
                    onClick = onRefresh
                )
            }
        }

        ErrorCallout(error, Modifier.fillMaxWidth())

        when {
            campaigns == null -> Unit
            campaigns.isEmpty() -> EmptyState(
                text = stringResource(if (participation == Participation.GAME_MASTER) Res.string.no_mastered_campaigns else Res.string.no_played_campaigns),
                modifier = Modifier.fillMaxWidth(),
            )

            else -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                campaigns.forEach { campaign ->
                    CampaignCard(
                        campaign = campaign,
                        nextSession = today?.let { NextSession.of(campaign.nextSession, it) },
                        imageUrl = imageUrl(campaign.imageId),
                        onClick = onSelect?.let { { it(campaign.id) } })
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            footer()
            Spacer(Modifier.weight(1f))
            when (participation) {
                Participation.GAME_MASTER -> Button(
                    text = stringResource(Res.string.create_campaign),
                    onClick = onCreate,
                    leadingIcon = IconVector.ADD
                )

                Participation.PLAYER -> Button(
                    text = stringResource(Res.string.join_campaign),
                    onClick = onJoin,
                    leadingIcon = IconVector.ADD
                )
            }
        }
    }
}

@Composable
private fun CampaignCard(
    campaign: CampaignOverviewResponseBody, nextSession: NextSession?, imageUrl: String, onClick: (() -> Unit)?
) {
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CampaignImage(url = imageUrl, contentDescription = campaign.name, size = 80.dp)
            Column(Modifier.weight(1f)) {
                Text(text = campaign.name, style = Theme.typography.title)
                Text(
                    text = pluralStringResource(Res.plurals.players, campaign.players, campaign.players),
                    style = Theme.typography.bodySmall
                )
            }
            nextSession?.let { NextSessionThumbnail(it) }
        }
    }

    if (onClick != null) {
        Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = Theme.shapes.roundedSmall) { content() }
    } else {
        Surface(modifier = Modifier.fillMaxWidth(), shape = Theme.shapes.roundedSmall) { content() }
    }
}

@Composable
private fun NextSessionThumbnail(nextSession: NextSession) {
    val color = when (nextSession) {
        is NextSession.InDays -> Theme.colors.textPrimary
        NextSession.None -> Theme.colors.textSecondary
        is NextSession.On -> Theme.colors.textSecondary
        NextSession.Today -> Theme.colors.primary
    }
    val caption = when (nextSession) {
        NextSession.Today -> stringResource(Res.string.session_today)
        is NextSession.InDays -> pluralStringResource(Res.plurals.session_in_days, nextSession.days)
        is NextSession.On -> nextSession.date.shortMonth()
        NextSession.None -> stringResource(Res.string.no_session_planned)
    }

    Thumbnail(Modifier.size(width = 100.dp, height = 56.dp)) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (nextSession) {
                NextSession.Today, NextSession.None -> Icon(vector = IconVector.CALENDAR, tint = color)
                is NextSession.InDays -> Text(
                    text = nextSession.days.toString(), style = Theme.typography.title, color = color, maxLines = 1
                )

                is NextSession.On -> Text(
                    text = nextSession.date.day.toString(), style = Theme.typography.title, color = color, maxLines = 1
                )
            }
            Text(
                text = caption,
                style = Theme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                color = color,
                maxLines = 2
            )
        }
    }
}

private fun LocalDate.shortMonth(): String =
    java.time.Month.of(month.number).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
