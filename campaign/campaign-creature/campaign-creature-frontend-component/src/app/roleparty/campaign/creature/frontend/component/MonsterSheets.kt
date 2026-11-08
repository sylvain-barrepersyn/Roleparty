package app.roleparty.campaign.creature.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.campaign.creature.frontend.component.resources.Res
import app.roleparty.campaign.creature.frontend.component.resources.add_monster
import app.roleparty.campaign.creature.frontend.component.resources.no_monsters
import app.roleparty.campaign.creature.frontend.component.resources.search
import app.roleparty.campaign.creature.frontend.component.resources.select_monster
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.field.text.SearchField
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.CenteredBox
import app.roleparty.core.designsystem.layout.ListDetailLayout
import app.roleparty.core.designsystem.layout.ListItem
import app.roleparty.media.frontend.component.ImageThumbnail
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import org.jetbrains.compose.resources.stringResource

@Composable
fun MonsterSheets(
    overviews: List<MonsterSheetOverviewResponseBody>,
    selectedId: MonsterSheetId?,
    error: String?,
    imageUrl: (ImageId) -> String,
    onSelect: (MonsterSheetId) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
    detail: @Composable () -> Unit,
) {
    var search by remember { mutableStateOf("") }
    val displayed = overviews.filter { it.name.contains(search.trim(), ignoreCase = true) }

    ListDetailLayout(
        modifier = modifier,
        listToolbar = {
            IconButton(icon = IconVector.ADD, contentDescription = stringResource(Res.string.add_monster), onClick = onCreate)
            SearchField(value = search, onValueChange = { search = it }, placeholder = stringResource(Res.string.search), modifier = Modifier.weight(1f))
        },
        list = {
            error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth()) }
            if (overviews.isEmpty()) EmptyState(stringResource(Res.string.no_monsters))
            displayed.forEach { monster ->
                ListItem(
                    selected = monster.id == selectedId,
                    onClick = { onSelect(monster.id) },
                    thumbnail = { ImageThumbnail(url = monster.imageId?.let(imageUrl), contentDescription = monster.name, placeholder = IconVector.MONSTER) },
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = monster.name, style = Theme.typography.title, maxLines = 1)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(vector = IconVector.MONSTER, tint = Theme.colors.textSecondary, size = 16.dp)
                            Text(
                                text = "${monster.size.label(monster.type)} ${monster.type.label()} (${monster.challengeRating.label()})",
                                style = Theme.typography.bodySmall,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        },
        detail = {
            if (selectedId == null) CenteredBox { Text(stringResource(Res.string.select_monster)) } else detail()
        },
    )
}
