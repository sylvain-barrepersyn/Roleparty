package app.roleparty.campaign.creature.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.contract.web.ChallengeRatingName
import app.roleparty.campaign.creature.contract.web.CreatureLanguageName
import app.roleparty.campaign.creature.contract.web.CreatureSizeName
import app.roleparty.campaign.creature.contract.web.CreatureSkillName
import app.roleparty.campaign.creature.contract.web.CreatureTypeName
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody.FeatureBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody.LegendaryActionBody
import app.roleparty.campaign.creature.contract.web.MovementModeName
import app.roleparty.campaign.creature.contract.web.ConditionName
import app.roleparty.campaign.creature.contract.web.CreatureAlignmentName
import app.roleparty.campaign.creature.contract.web.DamageTypeName
import app.roleparty.campaign.creature.contract.web.HabitatName
import app.roleparty.campaign.creature.contract.web.InitiativeProficiencyName
import app.roleparty.campaign.creature.contract.web.SenseName
import app.roleparty.campaign.creature.contract.web.TreasureName
import app.roleparty.campaign.creature.frontend.component.resources.*
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.dialog.Dialog
import app.roleparty.core.designsystem.field.Field
import app.roleparty.core.designsystem.field.number.IntField
import app.roleparty.core.designsystem.field.select.FieldOptionRow
import app.roleparty.core.designsystem.field.select.MultiSelectField
import app.roleparty.core.designsystem.field.select.ScrollableOptionList
import app.roleparty.core.designsystem.field.select.SingleSelectField
import app.roleparty.core.designsystem.field.text.RichTextField
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import org.jetbrains.compose.resources.stringResource

private const val FEET_STEP = 5

@Composable
internal fun MonsterSheetDialogs(
    dialog: SheetDialog?,
    sheet: MonsterSheetBody,
    onChange: (MonsterSheetBody) -> Unit,
    onDismiss: () -> Unit,
    imageField: ImageFieldSlot,
) {
    when (dialog) {
        null, SheetDialog.Notes -> Unit
        SheetDialog.Image -> ImageDialog(sheet.imageId, imageField, onDismiss) { onChange(sheet.copy(imageId = it)) }
        SheetDialog.Name -> NameDialog(sheet.name, onDismiss) { onChange(sheet.copy(name = it)) }
        SheetDialog.Taxonomy -> TaxonomyDialog(sheet, onDismiss, onChange)
        SheetDialog.Initiative -> InitiativeDialog(sheet, onDismiss) { onChange(sheet.copy(initiative = it)) }
        SheetDialog.ArmorClass -> ArmorClassDialog(sheet.armorClass, onDismiss) { onChange(sheet.copy(armorClass = it)) }
        SheetDialog.HitPoints -> HitPointsDialog(sheet.hitPoints, onDismiss) { onChange(sheet.copy(hitPoints = it)) }
        SheetDialog.Senses -> SensesDialog(sheet.senses, sheet.passivePerception, onDismiss) { senses, passivePerception ->
            onChange(sheet.copy(senses = senses, passivePerception = passivePerception))
        }
        SheetDialog.Speeds -> SpeedsDialog(sheet.speeds, sheet.hover, onDismiss) { speeds, hover -> onChange(sheet.copy(speeds = speeds, hover = hover)) }
        SheetDialog.Defenses -> DefensesDialog(sheet.defenses, onDismiss) { onChange(sheet.copy(defenses = it)) }
        SheetDialog.Gear -> GearDialog(sheet.gear, onDismiss) { onChange(sheet.copy(gear = it)) }
        SheetDialog.Ecology -> EcologyDialog(sheet.habitats, sheet.treasures, onDismiss) { habitats, treasures -> onChange(sheet.copy(habitats = habitats, treasures = treasures)) }
        SheetDialog.Languages -> LanguagesDialog(sheet.languages, onDismiss) { onChange(sheet.copy(languages = it)) }
        SheetDialog.ChallengeRating -> ChallengeRatingDialog(sheet.challengeRating, sheet.lair, onDismiss) { rating, lair -> onChange(sheet.copy(challengeRating = rating, lair = lair)) }
        SheetDialog.Abilities -> AbilitiesDialog(sheet.abilities, sheet.savingThrows, sheet.skills, onDismiss) { abilities, savingThrows, skills ->
            onChange(sheet.copy(abilities = abilities, savingThrows = savingThrows, skills = skills))
        }
        SheetDialog.LegendaryDescription -> LegendaryDescriptionDialog(sheet.legendaryActions, onDismiss) { onChange(sheet.copy(legendaryActions = it)) }
        is SheetDialog.Feature -> {
            val features = dialog.kind.of(sheet)
            FeatureDialog(
                initial = dialog.index?.let(features::getOrNull),
                onDismiss = onDismiss,
                onDelete = dialog.index?.let { index -> { onChange(dialog.kind.replace(sheet, features.filterIndexed { i, _ -> i != index })) } },
                onConfirm = { feature ->
                    val updated = if (dialog.index == null) features + feature else features.mapIndexed { i, it -> if (i == dialog.index) feature else it }
                    onChange(dialog.kind.replace(sheet, updated))
                },
            )
        }
        is SheetDialog.LegendaryAction -> {
            val items = sheet.legendaryActions.items
            fun withItems(updated: List<LegendaryActionBody>) = sheet.copy(legendaryActions = sheet.legendaryActions.copy(items = updated))
            LegendaryActionDialog(
                initial = dialog.index?.let(items::getOrNull),
                onDismiss = onDismiss,
                onDelete = dialog.index?.let { index -> { onChange(withItems(items.filterIndexed { i, _ -> i != index })) } },
                onConfirm = { action ->
                    onChange(withItems(if (dialog.index == null) items + action else items.mapIndexed { i, it -> if (i == dialog.index) action else it }))
                },
            )
        }
    }
}

@Composable
private fun EditionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    width: Dp = 800.dp,
    canConfirm: Boolean = true,
    onDelete: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(displayed = true, onDismiss = onDismiss, modifier = Modifier.width(width)) {
        Column(Modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            content()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(leadingIcon = IconVector.CANCEL, text = stringResource(Res.string.cancel), onClick = onDismiss)
                Spacer(Modifier.weight(1f))
                onDelete?.let { Button(leadingIcon = IconVector.DELETE, text = stringResource(Res.string.delete), onClick = it) }
                Button(leadingIcon = IconVector.CONFIRM, text = stringResource(Res.string.confirm), onClick = onConfirm, enabled = canConfirm)
            }
        }
    }
}

@Composable
private fun ImageDialog(initial: ImageId?, imageField: ImageFieldSlot, onDismiss: () -> Unit, onConfirm: (ImageId) -> Unit) {
    var image by remember { mutableStateOf(initial) }
    EditionDialog(onDismiss, onConfirm = { image?.let(onConfirm) }, canConfirm = image != null) {
        imageField(stringResource(Res.string.monster_image), image, { image = it }, Modifier.fillMaxWidth())
    }
}

@Composable
private fun NameDialog(initial: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(initial) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(name.trim()) }, canConfirm = name.isNotBlank()) {
        StringField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(Res.string.monster_name),
            required = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TaxonomyDialog(initial: MonsterSheetBody, onDismiss: () -> Unit, onConfirm: (MonsterSheetBody) -> Unit) {
    var size by remember { mutableStateOf(initial.size) }
    var type by remember { mutableStateOf(initial.type) }
    var alignment by remember { mutableStateOf(initial.alignment) }
    var alternativeSize by remember { mutableStateOf(initial.taxonomy.alternativeSize) }
    var swarmOf by remember { mutableStateOf(initial.taxonomy.swarmOf) }
    var tags by remember { mutableStateOf(initial.taxonomy.tags) }
    val valid = alternativeSize != size && swarmOf.let { it == null || it < size }
    EditionDialog(
        onDismiss,
        onConfirm = {
            onConfirm(initial.copy(size = size, type = type, alignment = alignment, taxonomy = MonsterSheetBody.TaxonomyBody(alternativeSize, swarmOf, tags.trim())))
        },
        canConfirm = valid,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SingleSelectField(
                value = size,
                onValueChange = { size = it },
                options = CreatureSizeName.entries,
                label = stringResource(Res.string.monster_size),
                optionLabel = { it.label() },
                required = true,
                maxListHeight = 230.dp,
                modifier = Modifier.weight(1f),
            )
            SingleSelectField(
                value = type,
                onValueChange = { type = it },
                options = CreatureTypeName.entries,
                label = stringResource(Res.string.monster_type),
                optionLabel = { it.label() },
                sortByLabel = true,
                required = true,
                maxListHeight = 230.dp,
                modifier = Modifier.weight(1f),
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OptionalSizeField(stringResource(Res.string.alternative_size), alternativeSize, { it != size }, { alternativeSize = it }, Modifier.weight(1f))
            OptionalSizeField(stringResource(Res.string.swarm), swarmOf, { it < size }, { swarmOf = it }, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StringField(value = tags, onValueChange = { tags = it }, label = stringResource(Res.string.type_tags), placeholder = "Goblinoid", modifier = Modifier.weight(1f))
            SingleSelectField(
                value = alignment,
                onValueChange = { alignment = it },
                options = CreatureAlignmentName.entries,
                label = stringResource(Res.string.alignment),
                optionLabel = { it.label() },
                required = true,
                maxListHeight = 230.dp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OptionalSizeField(
    label: String,
    value: CreatureSizeName?,
    available: (CreatureSizeName) -> Boolean,
    onChange: (CreatureSizeName?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Field(
        label = label,
        modifier = modifier,
        headerTrailing = { Text(value?.label() ?: stringResource(Res.string.none), color = Theme.colors.textPrimary) },
    ) {
        ScrollableOptionList(maxHeight = 200.dp) {
            FieldOptionRow(label = stringResource(Res.string.none), selected = value == null, onClick = { onChange(null) })
            CreatureSizeName.entries.filter(available).forEach { size ->
                FieldOptionRow(label = size.label(), selected = value == size, onClick = { onChange(size) })
            }
        }
    }
}

@Composable
private fun InitiativeDialog(sheet: MonsterSheetBody, onDismiss: () -> Unit, onConfirm: (InitiativeProficiencyName) -> Unit) {
    var proficiency by remember { mutableStateOf(sheet.initiative) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(proficiency) }, width = 400.dp) {
        Field(
            label = stringResource(Res.string.initiative),
            required = true,
            headerTrailing = { Text(initiativeOf(sheet.copy(initiative = proficiency)).signed(), color = Theme.colors.textPrimary) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            InitiativeProficiencyName.entries.forEach { option ->
                FieldOptionRow(
                    label = "${option.label()} (${initiativeOf(sheet.copy(initiative = option)).signed()})",
                    selected = proficiency == option,
                    onClick = { proficiency = option },
                )
            }
        }
    }
}

@Composable
private fun ArmorClassDialog(initial: MonsterSheetBody.ArmorClassBody, onDismiss: () -> Unit, onConfirm: (MonsterSheetBody.ArmorClassBody) -> Unit) {
    var armorClass by remember { mutableStateOf(initial) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(armorClass.copy(detail = armorClass.detail?.trim()?.ifBlank { null })) }) {
        Field(label = stringResource(Res.string.armor_class), required = true, headerTrailing = { Text(armorClass.value.toString(), color = Theme.colors.textPrimary) }) {
            StepperRow(stringResource(Res.string.value), armorClass.value.toString(), onDecrement = { armorClass = armorClass.copy(value = armorClass.value - 1) }) {
                armorClass = armorClass.copy(value = armorClass.value + 1)
            }
            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = armorClass.detail.orEmpty(),
                    onValueChange = { armorClass = armorClass.copy(detail = it) },
                    singleLine = true,
                    textStyle = Theme.typography.body.copy(color = Theme.colors.textPrimary, textAlign = TextAlign.End),
                    cursorBrush = SolidColor(Theme.colors.textPrimary),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(Res.string.details))
                            Spacer(Modifier.weight(1f))
                            inner()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun HitPointsDialog(initial: MonsterSheetBody.HitPointsBody, onDismiss: () -> Unit, onConfirm: (MonsterSheetBody.HitPointsBody) -> Unit) {
    var max by remember { mutableStateOf<Int?>(initial.max) }
    var dice by remember { mutableStateOf(initial.dice) }
    EditionDialog(onDismiss, onConfirm = { max?.let { onConfirm(MonsterSheetBody.HitPointsBody(it, dice.trim())) } }, canConfirm = max != null && dice.isNotBlank()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IntField(value = max, onValueChange = { max = it }, label = stringResource(Res.string.hit_points), required = true, modifier = Modifier.weight(1f))
            StringField(
                value = dice,
                onValueChange = { dice = it },
                label = stringResource(Res.string.hit_dice),
                placeholder = "4d8+5",
                required = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SensesDialog(initialSenses: Map<SenseName, Int>, initialPassivePerception: Int, onDismiss: () -> Unit, onConfirm: (Map<SenseName, Int>, Int) -> Unit) {
    var senses by remember { mutableStateOf(initialSenses) }
    var passivePerception by remember { mutableStateOf(initialPassivePerception) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(senses, passivePerception) }) {
        Field(label = stringResource(Res.string.senses)) {
            SenseName.entries.forEach { sense ->
                val range = senses[sense] ?: 0
                StepperRow(
                    label = sense.label(),
                    value = if (range == 0) stringResource(Res.string.none) else stringResource(Res.string.feet, range),
                    decrementEnabled = range > 0,
                    onDecrement = { senses = if (range <= FEET_STEP) senses - sense else senses + (sense to range - FEET_STEP) },
                ) {
                    senses = senses + (sense to range + FEET_STEP)
                }
            }
        }
        Field(label = stringResource(Res.string.passive_perception), required = true) {
            StepperRow(stringResource(Res.string.value), passivePerception.toString(), decrementEnabled = passivePerception > 0, onDecrement = { passivePerception -= 1 }) {
                passivePerception += 1
            }
        }
    }
}

@Composable
private fun SpeedsDialog(initial: Map<MovementModeName, Int>, initialHover: Boolean, onDismiss: () -> Unit, onConfirm: (Map<MovementModeName, Int>, Boolean) -> Unit) {
    var speeds by remember { mutableStateOf(initial) }
    var hover by remember { mutableStateOf(initialHover) }
    val canHover = (speeds[MovementModeName.FLY] ?: 0) > 0
    EditionDialog(onDismiss, onConfirm = { onConfirm(speeds, hover && canHover) }, canConfirm = speeds.isNotEmpty()) {
        Field(label = stringResource(Res.string.speed), required = true) {
            MovementModeName.entries.forEach { mode ->
                val speed = speeds[mode] ?: 0
                StepperRow(
                    label = mode.label(),
                    value = if (speed == 0) stringResource(Res.string.none) else stringResource(Res.string.feet, speed),
                    decrementEnabled = speed > 0,
                    onDecrement = { speeds = if (speed <= FEET_STEP) speeds - mode else speeds + (mode to speed - FEET_STEP) },
                ) {
                    speeds = speeds + (mode to speed + FEET_STEP)
                }
            }
            FieldOptionRow(
                label = stringResource(Res.string.hover),
                selected = hover && canHover,
                onClick = { hover = !hover },
                enabled = canHover,
                role = Role.Checkbox,
            )
        }
    }
}

@Composable
private fun DefensesDialog(initial: MonsterSheetBody.DefensesBody, onDismiss: () -> Unit, onConfirm: (MonsterSheetBody.DefensesBody) -> Unit) {
    var defenses by remember { mutableStateOf(initial) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(defenses) }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DamageTypesField(stringResource(Res.string.vulnerabilities), defenses.vulnerabilities, Modifier.weight(1f)) {
                defenses = defenses.copy(vulnerabilities = it, resistances = defenses.resistances - it, immunities = defenses.immunities - it)
            }
            DamageTypesField(stringResource(Res.string.resistances), defenses.resistances, Modifier.weight(1f)) {
                defenses = defenses.copy(resistances = it, vulnerabilities = defenses.vulnerabilities - it, immunities = defenses.immunities - it)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DamageTypesField(stringResource(Res.string.immunities), defenses.immunities, Modifier.weight(1f)) {
                defenses = defenses.copy(immunities = it, vulnerabilities = defenses.vulnerabilities - it, resistances = defenses.resistances - it)
            }
            MultiSelectField(
                selectedValues = defenses.conditionImmunities,
                onSelectionChange = { defenses = defenses.copy(conditionImmunities = it) },
                options = ConditionName.entries,
                label = stringResource(Res.string.condition_immunities),
                optionLabel = { it.label() },
                sortByLabel = true,
                maxListHeight = 200.dp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DamageTypesField(label: String, selected: Set<DamageTypeName>, modifier: Modifier, onChange: (Set<DamageTypeName>) -> Unit) {
    MultiSelectField(
        selectedValues = selected,
        onSelectionChange = onChange,
        options = DamageTypeName.entries,
        label = label,
        optionLabel = { it.label() },
        sortByLabel = true,
        maxListHeight = 200.dp,
        modifier = modifier,
    )
}

@Composable
private fun GearDialog(initial: List<String>, onDismiss: () -> Unit, onConfirm: (List<String>) -> Unit) {
    var text by remember { mutableStateOf(initial.joinToString("\n")) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(text.lines().map { it.trim() }.filter { it.isNotEmpty() }) }, width = 600.dp) {
        StringField(
            value = text,
            onValueChange = { text = it },
            label = stringResource(Res.string.gear),
            placeholder = stringResource(Res.string.gear_hint),
            singleLine = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EcologyDialog(
    initialHabitats: Set<HabitatName>,
    initialTreasures: Set<TreasureName>,
    onDismiss: () -> Unit,
    onConfirm: (Set<HabitatName>, Set<TreasureName>) -> Unit,
) {
    var habitats by remember { mutableStateOf(initialHabitats) }
    var treasures by remember { mutableStateOf(initialTreasures) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(habitats, treasures) }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MultiSelectField(
                selectedValues = habitats,
                onSelectionChange = { habitats = it },
                options = HabitatName.entries,
                label = stringResource(Res.string.habitat),
                optionLabel = { it.label() },
                sortByLabel = true,
                maxListHeight = 230.dp,
                modifier = Modifier.weight(1f),
            )
            MultiSelectField(
                selectedValues = treasures,
                onSelectionChange = { treasures = it },
                options = TreasureName.entries,
                label = stringResource(Res.string.treasure),
                optionLabel = { it.label() },
                sortByLabel = true,
                maxListHeight = 230.dp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LanguagesDialog(initial: MonsterSheetBody.LanguagesBody, onDismiss: () -> Unit, onConfirm: (MonsterSheetBody.LanguagesBody) -> Unit) {
    var known by remember { mutableStateOf(initial.known) }
    var understood by remember { mutableStateOf(initial.understood) }
    var other by remember { mutableStateOf(initial.other) }
    var telepathy by remember { mutableStateOf(initial.telepathyFeet) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(MonsterSheetBody.LanguagesBody(known, telepathy, understood - known, other.trim())) }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MultiSelectField(
                selectedValues = known,
                onSelectionChange = {
                    known = it
                    understood = understood - it
                },
                options = CreatureLanguageName.entries,
                label = stringResource(Res.string.languages),
                optionLabel = { it.label() },
                sortByLabel = true,
                maxListHeight = 200.dp,
                modifier = Modifier.weight(1f),
            )
            MultiSelectField(
                selectedValues = understood,
                onSelectionChange = {
                    understood = it
                    known = known - it
                },
                options = CreatureLanguageName.entries,
                label = stringResource(Res.string.understood_languages),
                optionLabel = { it.label() },
                sortByLabel = true,
                maxListHeight = 200.dp,
                modifier = Modifier.weight(1f),
            )
        }
        StringField(value = other, onValueChange = { other = it }, label = stringResource(Res.string.other_languages), modifier = Modifier.fillMaxWidth())
        val current = telepathy
        Field(
            label = stringResource(Res.string.telepathy),
            required = true,
            headerTrailing = { Text(current?.let { stringResource(Res.string.feet, it) } ?: stringResource(Res.string.no), color = Theme.colors.textPrimary) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            FieldOptionRow(label = stringResource(Res.string.no), selected = current == null, onClick = { telepathy = null })
            FieldOptionRow(
                label = stringResource(Res.string.yes),
                selected = current != null,
                onClick = { if (current == null) telepathy = 60 },
                trailingContent = {
                    if (current != null) {
                        Stepper(
                            value = stringResource(Res.string.feet, current),
                            onDecrement = { telepathy = (current - FEET_STEP).takeIf { it > 0 } },
                            onIncrement = { telepathy = current + FEET_STEP },
                            modifier = Modifier.padding(end = 16.dp),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun ChallengeRatingDialog(initial: ChallengeRatingName, initialLair: Boolean, onDismiss: () -> Unit, onConfirm: (ChallengeRatingName, Boolean) -> Unit) {
    var rating by remember { mutableStateOf(initial) }
    var lair by remember { mutableStateOf(initialLair) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(rating, lair) }, width = 400.dp) {
        SingleSelectField(
            value = rating,
            onValueChange = { rating = it },
            options = ChallengeRatingName.entries,
            label = stringResource(Res.string.challenge_rating),
            optionLabel = { it.label() },
            required = true,
            maxListHeight = 300.dp,
            modifier = Modifier.fillMaxWidth(),
        )
        Field(label = stringResource(Res.string.lair), modifier = Modifier.fillMaxWidth()) {
            FieldOptionRow(label = stringResource(Res.string.has_lair), selected = lair, onClick = { lair = !lair }, role = Role.Checkbox)
        }
    }
}

@Composable
private fun AbilitiesDialog(
    initialAbilities: MonsterSheetBody.AbilitiesBody,
    initialSavingThrows: MonsterSheetBody.SavingThrowsBody,
    initialSkills: Map<CreatureSkillName, Int>,
    onDismiss: () -> Unit,
    onConfirm: (MonsterSheetBody.AbilitiesBody, MonsterSheetBody.SavingThrowsBody, Map<CreatureSkillName, Int>) -> Unit,
) {
    var abilities by remember { mutableStateOf(initialAbilities) }
    var savingThrows by remember { mutableStateOf(initialSavingThrows) }
    var skills by remember { mutableStateOf(initialSkills) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(abilities, savingThrows, skills.filterValues { it > 0 }) }) {
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Ability.entries.forEach { ability ->
                    val score = ability.of(abilities)
                    val savingThrow = ability.of(savingThrows)
                    Field(label = stringResource(ability.label), modifier = Modifier.fillMaxWidth()) {
                        StepperRow(
                            label = stringResource(Res.string.ability_score),
                            value = "$score (${score.abilityModifier().signed()})",
                            decrementEnabled = score > 1,
                            onDecrement = { abilities = ability.with(abilities, score - 1) },
                        ) {
                            abilities = ability.with(abilities, score + 1)
                        }
                        StepperRow(
                            label = stringResource(Res.string.saving_throws),
                            value = (score.abilityModifier() + savingThrow).signed(),
                            color = if (savingThrow > 0) Theme.colors.textPrimary else Theme.colors.textSecondary,
                            decrementEnabled = savingThrow > 0,
                            onDecrement = { savingThrows = ability.with(savingThrows, savingThrow - 1) },
                        ) {
                            savingThrows = ability.with(savingThrows, savingThrow + 1)
                        }
                    }
                }
            }
            SkillsField(abilities, skills, onChange = { skills = it }, modifier = Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Composable
private fun SkillsField(
    abilities: MonsterSheetBody.AbilitiesBody,
    skills: Map<CreatureSkillName, Int>,
    onChange: (Map<CreatureSkillName, Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val summary = CreatureSkillName.entries.filter { (skills[it] ?: 0) > 0 }.map { skill ->
        "${skill.label()} ${(skill.ability.of(abilities).abilityModifier() + skills.getValue(skill)).signed()}"
    }.joinToString()

    Field(
        label = stringResource(Res.string.skills),
        modifier = modifier,
        headerTrailing = { Text(text = summary, color = Theme.colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    ) {
        ScrollableOptionList(maxHeight = 1000.dp) {
            CreatureSkillName.entries.forEach { skill ->
                val bonus = skills[skill] ?: 0
                StepperRow(
                    label = skill.label(),
                    value = (skill.ability.of(abilities).abilityModifier() + bonus).signed(),
                    color = if (bonus > 0) Theme.colors.textPrimary else Theme.colors.textSecondary,
                    decrementEnabled = bonus > 0,
                    onDecrement = { onChange(skills + (skill to bonus - 1)) },
                ) {
                    onChange(skills + (skill to bonus + 1))
                }
            }
        }
    }
}

@Composable
private fun LegendaryDescriptionDialog(initial: MonsterSheetBody.LegendaryActionsBody, onDismiss: () -> Unit, onConfirm: (MonsterSheetBody.LegendaryActionsBody) -> Unit) {
    var legendary by remember { mutableStateOf(initial) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(legendary) }, width = 600.dp) {
        val uses = legendary.uses
        val inLair = legendary.usesInLair
        Field(label = stringResource(Res.string.legendary_uses), required = true, modifier = Modifier.fillMaxWidth()) {
            StepperRow(
                label = stringResource(Res.string.legendary_uses),
                value = uses.toString(),
                decrementEnabled = uses > 1,
                onDecrement = { legendary = legendary.copy(uses = uses - 1) },
            ) {
                legendary = legendary.copy(uses = uses + 1, usesInLair = inLair?.coerceAtLeast(uses + 1))
            }
            FieldOptionRow(
                label = stringResource(Res.string.legendary_uses_in_lair),
                selected = inLair != null,
                onClick = { legendary = legendary.copy(usesInLair = if (inLair == null) uses + 1 else null) },
                role = Role.Checkbox,
                trailingContent = {
                    if (inLair != null) {
                        Stepper(
                            value = inLair.toString(),
                            onDecrement = { legendary = legendary.copy(usesInLair = inLair - 1) },
                            onIncrement = { legendary = legendary.copy(usesInLair = inLair + 1) },
                            decrementEnabled = inLair > uses,
                            modifier = Modifier.padding(end = 16.dp),
                        )
                    }
                },
            )
        }
        RichTextField(
            markdown = legendary.description,
            onMarkdownChange = { legendary = legendary.copy(description = it) },
            label = stringResource(Res.string.legendary_actions_description),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
internal fun NotesDialog(initial: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var notes by remember { mutableStateOf(initial) }
    EditionDialog(onDismiss, onConfirm = { onConfirm(notes.trim()) }, width = 600.dp) {
        RichTextField(markdown = notes, onMarkdownChange = { notes = it }, label = stringResource(Res.string.notes), modifier = Modifier.fillMaxWidth())
    }
}

@Composable
internal fun UnlinkDialog(monster: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(displayed = true, onDismiss = onDismiss, modifier = Modifier.width(480.dp)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = stringResource(Res.string.unlink_confirmation, monster), color = Theme.colors.textPrimary)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(leadingIcon = IconVector.CANCEL, text = stringResource(Res.string.cancel), onClick = onDismiss)
                Button(leadingIcon = IconVector.CONFIRM, text = stringResource(Res.string.unlink), onClick = onConfirm)
            }
        }
    }
}

@Composable
private fun FeatureDialog(initial: FeatureBody?, onDismiss: () -> Unit, onDelete: (() -> Unit)?, onConfirm: (FeatureBody) -> Unit) {
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var description by remember { mutableStateOf(initial?.description.orEmpty()) }
    EditionDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(FeatureBody(name.trim(), description.trim())) },
        width = 600.dp,
        canConfirm = name.isNotBlank() && description.isNotBlank(),
        onDelete = onDelete,
    ) {
        FeatureFields(name, { name = it }, description, { description = it })
    }
}

@Composable
private fun LegendaryActionDialog(initial: LegendaryActionBody?, onDismiss: () -> Unit, onDelete: (() -> Unit)?, onConfirm: (LegendaryActionBody) -> Unit) {
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var description by remember { mutableStateOf(initial?.description.orEmpty()) }
    var cost by remember { mutableStateOf(initial?.cost ?: 1) }
    EditionDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(LegendaryActionBody(name.trim(), description.trim(), cost)) },
        width = 600.dp,
        canConfirm = name.isNotBlank() && description.isNotBlank(),
        onDelete = onDelete,
    ) {
        FeatureFields(name, { name = it }, description, { description = it })
        Field(label = stringResource(Res.string.legendary_action_cost), required = true, modifier = Modifier.fillMaxWidth()) {
            StepperRow(stringResource(Res.string.value), cost.toString(), decrementEnabled = cost > 1, onDecrement = { cost -= 1 }) { cost += 1 }
        }
    }
}

@Composable
private fun FeatureFields(name: String, onNameChange: (String) -> Unit, description: String, onDescriptionChange: (String) -> Unit) {
    StringField(value = name, onValueChange = onNameChange, label = stringResource(Res.string.feature_name), required = true, modifier = Modifier.fillMaxWidth())
    RichTextField(
        markdown = description,
        onMarkdownChange = onDescriptionChange,
        label = stringResource(Res.string.feature_description),
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    color: Color = Theme.colors.textPrimary,
    decrementEnabled: Boolean = true,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = color)
        Spacer(Modifier.weight(1f))
        Stepper(value, onDecrement, onIncrement, color = color, decrementEnabled = decrementEnabled)
    }
}

@Composable
private fun Stepper(
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.textPrimary,
    decrementEnabled: Boolean = true,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            icon = IconVector.SUBTRACT,
            contentDescription = stringResource(Res.string.decrement),
            onClick = onDecrement,
            enabled = decrementEnabled,
            size = ButtonSize.TINY,
            shape = Theme.shapes.circle,
        )
        Text(text = value, color = color)
        IconButton(
            icon = IconVector.ADD,
            contentDescription = stringResource(Res.string.increment),
            onClick = onIncrement,
            size = ButtonSize.TINY,
            shape = Theme.shapes.circle,
        )
    }
}
