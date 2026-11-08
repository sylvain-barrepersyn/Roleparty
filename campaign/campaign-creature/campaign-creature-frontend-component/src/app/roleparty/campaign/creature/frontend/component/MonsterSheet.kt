package app.roleparty.campaign.creature.frontend.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.contract.web.CreatureSkillName
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody.FeatureBody
import app.roleparty.campaign.creature.contract.web.MovementModeName
import app.roleparty.campaign.creature.frontend.component.resources.*
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.divider.HorizontalDivider
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.feedback.Loader
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.MarkdownText
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.RemoteImage
import java.text.NumberFormat
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

typealias ImageFieldSlot = @Composable (label: String, value: ImageId?, onValueChange: (ImageId) -> Unit, modifier: Modifier) -> Unit

@Composable
fun MonsterSheet(
    sheet: MonsterSheetBody,
    editing: Boolean,
    saving: Boolean,
    error: String?,
    imageUrl: (ImageId) -> String,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onChange: (MonsterSheetBody) -> Unit,
    modifier: Modifier = Modifier,
    imageField: ImageFieldSlot,
) {
    var dialog by remember { mutableStateOf<SheetDialog?>(null) }
    val open: (SheetDialog) -> Unit = { dialog = it }

    Column(modifier.fillMaxSize()) {
        Toolbar(editing = editing, saving = saving, onEdit = onEdit, onDuplicate = onDuplicate, onCancel = onCancel, onSave = onSave)
        HorizontalDivider()
        error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)) }

        Row(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(32.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Header(sheet, imageUrl, editing, open)

                    EditableRow(editing, onEdit = { open(SheetDialog.Speeds) }) {
                        StatLine(stringResource(Res.string.speed), speedsText(sheet))
                    }
                    EditableRow(editing, onEdit = { open(SheetDialog.Senses) }) {
                        StatLine(stringResource(Res.string.senses), sensesText(sheet))
                    }
                    DefenseLine(Res.string.vulnerabilities, sheet.defenses.vulnerabilities.map { it.label() }, editing, open)
                    DefenseLine(Res.string.resistances, sheet.defenses.resistances.map { it.label() }, editing, open)
                    DefenseLine(Res.string.immunities, immunitiesText(sheet), editing, open)
                    EditableRow(editing, onEdit = { open(SheetDialog.Languages) }) {
                        StatLine(stringResource(Res.string.languages), languagesText(sheet))
                    }
                    if (editing || sheet.gear.isNotEmpty()) {
                        EditableRow(editing, onEdit = { open(SheetDialog.Gear) }) {
                            StatLine(stringResource(Res.string.gear), sheet.gear.joinToString().ifEmpty { stringResource(Res.string.none) })
                        }
                    }
                    EditableRow(editing, onEdit = { open(SheetDialog.ChallengeRating) }) {
                        StatLine(stringResource(Res.string.challenge_rating), challengeRatingText(sheet))
                    }
                    if (editing || sheet.habitats.isNotEmpty() || sheet.treasures.isNotEmpty()) {
                        EditableRow(editing, onEdit = { open(SheetDialog.Ecology) }) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatLine(stringResource(Res.string.habitat), sheet.habitats.labels { it.label() })
                                StatLine(stringResource(Res.string.treasure), sheet.treasures.labels { it.label() })
                            }
                        }
                    }
                }

                FeatureKind.entries.forEach { kind ->
                    val features = kind.of(sheet)
                    if (editing || features.isNotEmpty()) {
                        Section(stringResource(kind.title), editing, onAdd = { open(SheetDialog.Feature(kind, null)) }) {
                            features.forEachIndexed { index, feature ->
                                FeatureItem("${feature.name}.", feature.description, editing, onEdit = { open(SheetDialog.Feature(kind, index)) })
                            }
                        }
                    }
                }

                val legendary = sheet.legendaryActions
                if (editing || legendary.items.isNotEmpty() || legendary.description.isNotBlank()) {
                    Section(stringResource(Res.string.legendary_actions), editing, onAdd = { open(SheetDialog.LegendaryAction(null)) }) {
                        EditableRow(editing, onEdit = { open(SheetDialog.LegendaryDescription) }) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                StatLine(stringResource(Res.string.legendary_uses), legendaryUsesText(legendary))
                                if (editing || legendary.description.isNotBlank()) MarkdownText(legendary.description.ifBlank { stringResource(Res.string.none) })
                            }
                        }
                        legendary.items.forEachIndexed { index, action ->
                            FeatureItem("${action.name} (${action.cost}).", action.description, editing, onEdit = { open(SheetDialog.LegendaryAction(index)) })
                        }
                    }
                }
            }

            VerticalAbilities(sheet.abilities, sheet.savingThrows, sheet.skills, editing, onEdit = { open(SheetDialog.Abilities) }, modifier = Modifier.width(220.dp))
        }
    }

    MonsterSheetDialogs(
        dialog = dialog.takeIf { editing },
        sheet = sheet,
        onChange = {
            onChange(it)
            dialog = null
        },
        onDismiss = { dialog = null },
        imageField = imageField,
    )
}

@Composable
private fun Toolbar(editing: Boolean, saving: Boolean, onEdit: () -> Unit, onDuplicate: () -> Unit, onCancel: () -> Unit, onSave: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (editing) {
            Button(leadingIcon = IconVector.CANCEL, text = stringResource(Res.string.cancel), onClick = onCancel, enabled = !saving)
            Button(leadingIcon = IconVector.CONFIRM, text = stringResource(Res.string.save), onClick = onSave, enabled = !saving)
            if (saving) Loader(size = 20.dp)
        } else {
            Button(leadingIcon = IconVector.EDIT, text = stringResource(Res.string.edit), onClick = onEdit)
            Button(leadingIcon = IconVector.COPY, text = stringResource(Res.string.duplicate), onClick = onDuplicate)
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun Header(sheet: MonsterSheetBody, imageUrl: (ImageId) -> String, editing: Boolean, open: (SheetDialog) -> Unit) {
    Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box {
            CreaturePicture(url = sheet.imageId?.let(imageUrl), contentDescription = sheet.name, modifier = Modifier.size(width = 180.dp, height = 144.dp))
            if (editing) EditButton(onClick = { open(SheetDialog.Image) }, modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp))
        }
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column {
                EditableRow(editing, onEdit = { open(SheetDialog.Name) }) {
                    Text(text = sheet.name, style = Theme.typography.headline)
                }
                EditableRow(editing, onEdit = { open(SheetDialog.Taxonomy) }) {
                    Text(text = taxonomyText(sheet))
                }
            }
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EditableRow(editing, onEdit = { open(SheetDialog.ArmorClass) }, modifier = Modifier.weight(1f).fillMaxHeight()) {
                    StatBox(stringResource(Res.string.armor_class), sheet.armorClass.value.toString(), sheet.armorClass.detail?.ifBlank { null })
                }
                EditableRow(editing, onEdit = { open(SheetDialog.Initiative) }, modifier = Modifier.weight(1f).fillMaxHeight()) {
                    val initiative = initiativeOf(sheet)
                    StatBox(stringResource(Res.string.initiative), initiative.signed(), "(${10 + initiative})")
                }
                EditableRow(editing, onEdit = { open(SheetDialog.HitPoints) }, modifier = Modifier.weight(1f).fillMaxHeight()) {
                    StatBox(stringResource(Res.string.hit_points), sheet.hitPoints.max.toString(), sheet.hitPoints.dice)
                }
            }
        }
    }
}

@Composable
private fun RowScope.StatBox(label: String, value: String, detail: String?) {
    Column(
        modifier = Modifier.weight(1f).fillMaxHeight().subtleBorder().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, style = Theme.typography.bodySmall)
        Text(text = value, style = Theme.typography.bodyLarge, color = Theme.colors.textPrimary)
        Text(text = detail ?: stringResource(Res.string.none), style = Theme.typography.bodySmall, color = if (detail == null) Theme.colors.textSecondary else Theme.colors.textPrimary)
    }
}

@Composable
internal fun CreaturePicture(url: String?, contentDescription: String, modifier: Modifier = Modifier) {
    if (url == null) {
        Surface(modifier = modifier, contentAlignment = Alignment.Center) {
            Icon(vector = IconVector.MONSTER, tint = Theme.colors.textSecondary, size = 32.dp)
        }
    } else {
        RemoteImage(url = url, contentDescription = contentDescription, modifier = modifier.clip(Theme.shapes.rounded), contentScale = ContentScale.Crop)
    }
}

@Composable
private fun EditButton(onClick: () -> Unit, modifier: Modifier = Modifier) =
    IconButton(icon = IconVector.EDIT, contentDescription = stringResource(Res.string.edit), onClick = onClick, size = ButtonSize.TINY, modifier = modifier)

@Composable
private fun EditableRow(editing: Boolean, onEdit: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (editing) EditButton(onClick = onEdit)
        content()
    }
}

@Composable
private fun Section(title: String, editing: Boolean, onAdd: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (editing) IconButton(icon = IconVector.ADD, contentDescription = title, onClick = onAdd, size = ButtonSize.TINY)
                Text(text = title, style = Theme.typography.title)
            }
        }
        content()
    }
}

@Composable
private fun FeatureItem(label: String, description: String, editing: Boolean, onEdit: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (editing) EditButton(onClick = onEdit)
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = label, color = Theme.colors.textPrimary)
            MarkdownText(description)
        }
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$label:")
        Text(value)
    }
}

@Composable
private fun VerticalAbilities(
    abilities: MonsterSheetBody.AbilitiesBody,
    savingThrows: MonsterSheetBody.SavingThrowsBody,
    skills: Map<CreatureSkillName, Int>,
    editing: Boolean,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Column(Modifier.subtleBorder().padding(12.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
            Ability.entries.forEach { ability ->
                val score = ability.of(abilities)
                val savingThrow = ability.of(savingThrows)
                val savingThrowColor = if (savingThrow > 0) Theme.colors.textPrimary else Theme.colors.textSecondary
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${stringResource(ability.label)} ($score)", color = Theme.colors.textPrimary, modifier = Modifier.weight(1f))
                        Text(score.abilityModifier().signed(), style = Theme.typography.bodyLarge, color = Theme.colors.textPrimary)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(Res.string.saving_throws), style = Theme.typography.bodySmall, color = savingThrowColor, modifier = Modifier.weight(1f))
                        Text((score.abilityModifier() + savingThrow).signed(), color = savingThrowColor)
                    }
                    CreatureSkillName.entries.filter { it.ability == ability && (skills[it] ?: 0) > 0 }.forEach { skill ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(skill.label(), style = Theme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Text((score.abilityModifier() + skills.getValue(skill)).signed())
                        }
                    }
                }
            }
        }
        if (editing) EditButton(onClick = onEdit, modifier = Modifier.align(Alignment.TopCenter).offset(y = (-12).dp))
    }
}

@Composable
private fun DefenseLine(title: StringResource, values: List<String>, editing: Boolean, open: (SheetDialog) -> Unit) {
    if (!editing && values.isEmpty()) return
    EditableRow(editing, onEdit = { open(SheetDialog.Defenses) }) {
        StatLine(stringResource(title), values.joinToString().ifEmpty { stringResource(Res.string.none) })
    }
}

@Composable
private fun <T> Set<T>.labels(label: @Composable (T) -> String): String =
    map { label(it) }.sorted().joinToString().ifEmpty { stringResource(Res.string.none) }

internal fun initiativeOf(sheet: MonsterSheetBody) =
    sheet.abilities.dexterity.abilityModifier() + sheet.initiative.multiplier * sheet.challengeRating.proficiencyBonus

@Composable
private fun taxonomyText(sheet: MonsterSheetBody): String {
    val tags = sheet.taxonomy.tags.takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty()
    val swarmOf = sheet.taxonomy.swarmOf
    val alternativeSize = sheet.taxonomy.alternativeSize
    val creature = when {
        swarmOf != null -> stringResource(Res.string.swarm_of, sheet.size.label(), swarmOf.label(), sheet.type.pluralLabel() + tags)
        alternativeSize != null -> "${stringResource(Res.string.size_or, sheet.size.label(sheet.type), alternativeSize.label(sheet.type))} ${sheet.type.label()}$tags"
        else -> "${sheet.size.label(sheet.type)} ${sheet.type.label()}$tags"
    }
    return "$creature, ${sheet.alignment.label()}"
}

@Composable
private fun speedsText(sheet: MonsterSheetBody): String =
    sheet.speeds.entries.sortedBy { it.key.ordinal }
        .map { speed ->
            val hover = if (speed.key == MovementModeName.FLY && sheet.hover) " (${stringResource(Res.string.hover)})" else ""
            "${speed.key.label()} ${stringResource(Res.string.feet, speed.value)}$hover"
        }
        .ifEmpty { listOf(stringResource(Res.string.none)) }
        .joinToString()

@Composable
private fun sensesText(sheet: MonsterSheetBody): String {
    val senses = sheet.senses.entries.sortedBy { it.key.ordinal }.map { "${it.key.label()} ${stringResource(Res.string.feet, it.value)}" }
    return (senses + "${stringResource(Res.string.passive_perception)} ${sheet.passivePerception}").joinToString()
}

@Composable
private fun immunitiesText(sheet: MonsterSheetBody): List<String> {
    val damages = sheet.defenses.immunities.map { it.label() }.sorted().joinToString()
    val conditions = sheet.defenses.conditionImmunities.map { it.label() }.sorted().joinToString()
    return listOf(listOf(damages, conditions).filter { it.isNotEmpty() }.joinToString("; ")).filter { it.isNotEmpty() }
}

@Composable
private fun languagesText(sheet: MonsterSheetBody): String {
    val languages = sheet.languages
    val parts = buildList {
        languages.known.map { it.label() }.sorted().joinToString().takeIf { it.isNotEmpty() }?.let(::add)
        languages.understood.map { it.label() }.sorted().joinToString().takeIf { it.isNotEmpty() }?.let { add(stringResource(Res.string.understands, it)) }
        languages.other.takeIf { it.isNotBlank() }?.let(::add)
        languages.telepathyFeet?.let { add("${stringResource(Res.string.telepathy)} ${stringResource(Res.string.feet, it)}") }
    }
    return parts.joinToString("; ").ifEmpty { stringResource(Res.string.none) }
}

@Composable
private fun challengeRatingText(sheet: MonsterSheetBody): String {
    val format = NumberFormat.getIntegerInstance()
    val rating = sheet.challengeRating
    val experience = format.format(rating.experience).let {
        if (sheet.lair) stringResource(Res.string.experience_in_lair, it, format.format(rating.lairExperience)) else it
    }
    return "${rating.label()} (${stringResource(Res.string.experience)}: $experience; ${stringResource(Res.string.proficiency_bonus)} ${rating.proficiencyBonus.signed()})"
}

@Composable
private fun legendaryUsesText(legendary: MonsterSheetBody.LegendaryActionsBody): String {
    val inLair = legendary.usesInLair ?: return stringResource(Res.string.legendary_uses_value, legendary.uses)
    return stringResource(Res.string.legendary_uses_in_lair_value, legendary.uses, inLair)
}

@Composable
private fun Modifier.subtleBorder() = border(1.dp, Theme.colors.textSecondary.copy(alpha = 0.15f), Theme.shapes.roundedSmall)

internal enum class FeatureKind(val title: StringResource) {
    TRAITS(Res.string.traits),
    ACTIONS(Res.string.actions),
    BONUS_ACTIONS(Res.string.bonus_actions),
    REACTIONS(Res.string.reactions);

    fun of(sheet: MonsterSheetBody): List<FeatureBody> = when (this) {
        TRAITS -> sheet.traits
        ACTIONS -> sheet.actions
        BONUS_ACTIONS -> sheet.bonusActions
        REACTIONS -> sheet.reactions
    }

    fun replace(sheet: MonsterSheetBody, features: List<FeatureBody>): MonsterSheetBody = when (this) {
        TRAITS -> sheet.copy(traits = features)
        ACTIONS -> sheet.copy(actions = features)
        BONUS_ACTIONS -> sheet.copy(bonusActions = features)
        REACTIONS -> sheet.copy(reactions = features)
    }
}

internal enum class Ability(val label: StringResource) {
    STRENGTH(Res.string.strength),
    DEXTERITY(Res.string.dexterity),
    CONSTITUTION(Res.string.constitution),
    INTELLIGENCE(Res.string.intelligence),
    WISDOM(Res.string.wisdom),
    CHARISMA(Res.string.charisma);

    fun of(abilities: MonsterSheetBody.AbilitiesBody): Int = when (this) {
        STRENGTH -> abilities.strength
        DEXTERITY -> abilities.dexterity
        CONSTITUTION -> abilities.constitution
        INTELLIGENCE -> abilities.intelligence
        WISDOM -> abilities.wisdom
        CHARISMA -> abilities.charisma
    }

    fun of(savingThrows: MonsterSheetBody.SavingThrowsBody): Int = when (this) {
        STRENGTH -> savingThrows.strength
        DEXTERITY -> savingThrows.dexterity
        CONSTITUTION -> savingThrows.constitution
        INTELLIGENCE -> savingThrows.intelligence
        WISDOM -> savingThrows.wisdom
        CHARISMA -> savingThrows.charisma
    }

    fun with(savingThrows: MonsterSheetBody.SavingThrowsBody, bonus: Int): MonsterSheetBody.SavingThrowsBody = when (this) {
        STRENGTH -> savingThrows.copy(strength = bonus)
        DEXTERITY -> savingThrows.copy(dexterity = bonus)
        CONSTITUTION -> savingThrows.copy(constitution = bonus)
        INTELLIGENCE -> savingThrows.copy(intelligence = bonus)
        WISDOM -> savingThrows.copy(wisdom = bonus)
        CHARISMA -> savingThrows.copy(charisma = bonus)
    }

    fun with(abilities: MonsterSheetBody.AbilitiesBody, score: Int): MonsterSheetBody.AbilitiesBody = when (this) {
        STRENGTH -> abilities.copy(strength = score)
        DEXTERITY -> abilities.copy(dexterity = score)
        CONSTITUTION -> abilities.copy(constitution = score)
        INTELLIGENCE -> abilities.copy(intelligence = score)
        WISDOM -> abilities.copy(wisdom = score)
        CHARISMA -> abilities.copy(charisma = score)
    }
}

internal sealed interface SheetDialog {
    data object Image : SheetDialog
    data object Name : SheetDialog
    data object Taxonomy : SheetDialog
    data object ArmorClass : SheetDialog
    data object HitPoints : SheetDialog
    data object Initiative : SheetDialog
    data object Senses : SheetDialog
    data object Defenses : SheetDialog
    data object Gear : SheetDialog
    data object Ecology : SheetDialog
    data object Speeds : SheetDialog
    data object Languages : SheetDialog
    data object ChallengeRating : SheetDialog
    data object Abilities : SheetDialog
    data object LegendaryDescription : SheetDialog
    data class Feature(val kind: FeatureKind, val index: Int?) : SheetDialog
    data class LegendaryAction(val index: Int?) : SheetDialog
}
