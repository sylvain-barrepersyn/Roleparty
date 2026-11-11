package app.roleparty.campaign.creature.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.contract.web.*
import app.roleparty.campaign.creature.frontend.component.resources.*
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
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageThumbnail
import org.jetbrains.compose.resources.stringResource

sealed interface CreatureSelection {

    data class Monster(val monsterSheetId: MonsterSheetId) : CreatureSelection

    data class Character(val characterSheetId: CharacterSheetId) : CreatureSelection
}

private class CreatureRow(
    val selection: CreatureSelection,
    val name: String,
    val imageId: ImageId?,
    val icon: IconVector,
    val size: CreatureSizeName,
    val type: CreatureTypeName,
    val challengeRating: ChallengeRatingName,
    val linkedTo: String?,
) {
    companion object {
        fun from(monster: MonsterSheetOverviewResponseBody) = CreatureRow(
            CreatureSelection.Monster(monster.id),
            monster.name,
            monster.imageId,
            IconVector.MONSTER,
            monster.size,
            monster.type,
            monster.challengeRating,
            null
        )

        fun from(character: CharacterSheetOverviewResponseBody) = CreatureRow(
            CreatureSelection.Character(character.id),
            character.name,
            character.imageId,
            IconVector.PUPPET,
            character.size,
            character.type,
            character.challengeRating,
            character.monsterSheetId?.let { it.toString() })
    }
}

@Composable
fun Creatures(
    monsters: List<MonsterSheetOverviewResponseBody>,
    characters: List<CharacterSheetOverviewResponseBody>,
    selected: CreatureSelection?,
    error: String?,
    imageUrl: (ImageId) -> String,
    onSelect: (CreatureSelection) -> Unit,
    onCreateMonster: () -> Unit,
    modifier: Modifier = Modifier,
    detail: @Composable () -> Unit,
) {
    var search by remember { mutableStateOf("") }
    val rows = monsters.map(CreatureRow::from) + characters.map(CreatureRow::from)
    val displayed = rows.filter { it.name.contains(search.trim(), ignoreCase = true) }.sortedBy { it.name.lowercase() }

    ListDetailLayout(
        modifier = modifier,
        listToolbar = {
            IconButton(
                icon = IconVector.ADD,
                contentDescription = stringResource(Res.string.add_monster),
                onClick = onCreateMonster
            )
            SearchField(value = search, onValueChange = { search = it }, placeholder = stringResource(Res.string.search), modifier = Modifier.weight(1f))
        },
        list = {
            error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth()) }
            if (rows.isEmpty()) EmptyState(stringResource(Res.string.no_creatures))
            displayed.forEach { row ->
                ListItem(
                    selected = row.selection == selected,
                    onClick = { onSelect(row.selection) },
                    thumbnail = {
                        ImageThumbnail(
                            url = row.imageId?.let(imageUrl),
                            contentDescription = row.name,
                            placeholder = row.icon
                        )
                    },
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = row.name, style = Theme.typography.title, maxLines = 1)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(vector = row.icon, tint = Theme.colors.textSecondary, size = 16.dp)
                            val linked = row.linkedTo?.let { ", ${stringResource(Res.string.linked_to, it)}" }.orEmpty()
                            Text(
                                text = "${row.size.label(row.type)} ${row.type.label()} (${row.challengeRating.label()})$linked",
                                style = Theme.typography.bodySmall,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        },
        detail = {
            if (selected == null) CenteredBox { Text(stringResource(Res.string.select_creature)) } else detail()
        },
    )
}
