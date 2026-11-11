package app.roleparty.campaign.creature.frontend.component.playercharacter.creation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.contract.web.*
import app.roleparty.campaign.creature.frontend.component.playercharacter.ErrorCallout
import app.roleparty.campaign.creature.frontend.component.resources.*
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.divider.HorizontalDivider
import app.roleparty.core.designsystem.field.number.IntField
import app.roleparty.core.designsystem.field.select.MultiSelectField
import app.roleparty.core.designsystem.field.select.SingleSelectField
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.tab.Tab
import app.roleparty.core.designsystem.tab.TabGroup
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import app.roleparty.campaign.creature.frontend.component.signed

@Composable
fun PlayerCharacterCreationForm(
    draft: PlayerCharacterDraft,
    step: PlayerCharacterCreationStep,
    submitting: Boolean,
    error: String?,
    onDraftChange: (PlayerCharacterDraft) -> Unit,
    onStepChange: (PlayerCharacterCreationStep) -> Unit,
    onRollScores: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    imageField: @Composable (label: String, modifier: Modifier) -> Unit,
) {
    Column(modifier.fillMaxSize().padding(32.dp), Arrangement.spacedBy(16.dp), Alignment.CenterHorizontally) {
        Text(text = stringResource(Res.string.create_character), style = Theme.typography.headline, modifier = Modifier.fillMaxWidth())
        TabGroup(Modifier.fillMaxWidth()) {
            PlayerCharacterCreationStep.entries.forEach {
                Tab(label = "${it.ordinal + 1}. ${it.label()}", selected = it == step, onClick = { onStepChange(it) })
            }
        }

        Column(
            modifier = Modifier.widthIn(max = 1200.dp).weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (step) {
                PlayerCharacterCreationStep.CLASS -> ClassStep(draft, onDraftChange)
                PlayerCharacterCreationStep.ORIGIN -> OriginStep(draft, onDraftChange)
                PlayerCharacterCreationStep.ABILITY_SCORES -> AbilityScoresStep(draft, onDraftChange, onRollScores)
                PlayerCharacterCreationStep.ALIGNMENT -> AlignmentStep(draft, onDraftChange)
                PlayerCharacterCreationStep.DETAILS -> DetailsStep(draft, onDraftChange)
                PlayerCharacterCreationStep.NAME -> NameStep(draft, onDraftChange, imageField)
            }
        }

        ErrorCallout(error, Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                text = stringResource(Res.string.cancel),
                onClick = onCancel,
                leadingIcon = IconVector.CANCEL,
                enabled = !submitting
            )
            Spacer(Modifier.weight(1f))
            PlayerCharacterCreationStep.entries.getOrNull(step.ordinal - 1)?.let { previous ->
                Button(
                    text = stringResource(Res.string.previous_step),
                    onClick = { onStepChange(previous) },
                    leadingIcon = IconVector.PREVIOUS,
                    enabled = !submitting
                )
            }
            val next = PlayerCharacterCreationStep.entries.getOrNull(step.ordinal + 1)
            if (next != null) {
                Button(
                    text = stringResource(Res.string.next_step),
                    onClick = { onStepChange(next) },
                    leadingIcon = IconVector.NEXT,
                    enabled = draft.isComplete(step)
                )
            } else {
                Button(
                    text = stringResource(if (submitting) Res.string.creating else Res.string.create_character),
                    onClick = onSubmit,
                    leadingIcon = IconVector.CONFIRM,
                    enabled = draft.toRequestBody() != null && !submitting,
                )
            }
        }
    }
}

@Composable
private fun ClassStep(draft: PlayerCharacterDraft, onChange: (PlayerCharacterDraft) -> Unit) {
    Hint(stringResource(Res.string.class_hint))
    StringField(
        value = draft.className,
        onValueChange = { onChange(draft.copy(className = it)) },
        label = stringResource(Res.string.class_name),
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )
    SingleSelectField(
        value = draft.hitDie,
        onValueChange = { onChange(draft.copy(hitDie = it)) },
        options = HitDieName.entries,
        label = stringResource(Res.string.hit_die),
        optionLabel = { it.label() },
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )
    MultiSelectField(
        selectedValues = draft.savingThrows,
        onSelectionChange = { onChange(draft.copy(savingThrows = it)) },
        options = AbilityName.entries,
        label = stringResource(Res.string.saving_throw_proficiencies),
        optionLabel = { it.label() },
        modifier = Modifier.fillMaxWidth(),
    )
    MultiSelectField(
        selectedValues = draft.armorTrainings,
        onSelectionChange = { onChange(draft.copy(armorTrainings = it)) },
        options = ArmorTrainingName.entries,
        label = stringResource(Res.string.armor_training),
        optionLabel = { it.label() },
        modifier = Modifier.fillMaxWidth(),
    )
    MultiSelectField(
        selectedValues = draft.classSkills,
        onSelectionChange = { onChange(draft.copy(classSkills = it)) },
        options = SkillName.entries,
        label = stringResource(Res.string.class_skill_proficiencies),
        optionLabel = { it.label() },
        sortByLabel = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun OriginStep(draft: PlayerCharacterDraft, onChange: (PlayerCharacterDraft) -> Unit) {
    Section(stringResource(Res.string.background))
    StringField(
        value = draft.backgroundName,
        onValueChange = { onChange(draft.copy(backgroundName = it)) },
        label = stringResource(Res.string.background_name),
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StringField(
            value = draft.originFeat,
            onValueChange = { onChange(draft.copy(originFeat = it)) },
            label = stringResource(Res.string.origin_feat),
            modifier = Modifier.weight(1f),
        )
        StringField(
            value = draft.toolProficiency,
            onValueChange = { onChange(draft.copy(toolProficiency = it)) },
            label = stringResource(Res.string.tool_proficiency),
            modifier = Modifier.weight(1f),
        )
    }
    MultiSelectField(
        selectedValues = draft.backgroundSkills,
        onSelectionChange = { onChange(draft.copy(backgroundSkills = it)) },
        options = SkillName.entries,
        label = stringResource(Res.string.background_skill_proficiencies),
        optionLabel = { it.label() },
        sortByLabel = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Section(stringResource(Res.string.starting_equipment))
    StringField(
        value = draft.equipment,
        onValueChange = { onChange(draft.copy(equipment = it)) },
        label = stringResource(Res.string.equipment),
        placeholder = stringResource(Res.string.equipment_hint),
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )
    IntField(
        value = draft.coins,
        onValueChange = { onChange(draft.copy(coins = it)) },
        label = stringResource(Res.string.coins),
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Section(stringResource(Res.string.species))
    StringField(
        value = draft.speciesName,
        onValueChange = { onChange(draft.copy(speciesName = it)) },
        label = stringResource(Res.string.species_name),
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SingleSelectField(
            value = draft.size,
            onValueChange = { onChange(draft.copy(size = it)) },
            options = SizeName.entries,
            label = stringResource(Res.string.size),
            optionLabel = { it.label() },
            required = true,
            modifier = Modifier.weight(1f),
        )
        IntField(
            value = draft.speed,
            onValueChange = { onChange(draft.copy(speed = it)) },
            label = stringResource(Res.string.speed_feet),
            required = true,
            modifier = Modifier.weight(1f),
        )
    }
    StringField(
        value = draft.speciesTraits,
        onValueChange = { onChange(draft.copy(speciesTraits = it)) },
        label = stringResource(Res.string.species_traits),
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )

    Section(stringResource(Res.string.past_and_present))
    StringField(
        value = draft.backstory,
        onValueChange = { onChange(draft.copy(backstory = it)) },
        label = stringResource(Res.string.backstory),
        placeholder = stringResource(Res.string.backstory_hint),
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )
    StringField(
        value = draft.languages,
        onValueChange = { onChange(draft.copy(languages = it)) },
        label = stringResource(Res.string.languages),
        placeholder = stringResource(Res.string.languages_hint),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun AbilityScoresStep(
    draft: PlayerCharacterDraft,
    onChange: (PlayerCharacterDraft) -> Unit,
    onRoll: () -> Unit
) {
    Section(stringResource(Res.string.generate_scores))
    TabGroup(Modifier.fillMaxWidth()) {
        AbilityScoreMethod.entries.forEach {
            Tab(
                label = it.label(),
                selected = it == draft.scoreMethod,
                onClick = { if (it != draft.scoreMethod) onChange(draft.withScoreMethod(it)) })
        }
    }
    when (draft.scoreMethod) {
        AbilityScoreMethod.STANDARD_ARRAY -> Hint(
            stringResource(
                Res.string.standard_array_hint,
                draft.unassignedScores.joinToString().ifEmpty { "—" })
        )

        AbilityScoreMethod.RANDOM -> Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(text = stringResource(Res.string.roll_scores), onClick = onRoll, leadingIcon = IconVector.REFRESH)
            if (draft.rolledScores.isNotEmpty()) Hint(
                stringResource(
                    Res.string.standard_array_hint,
                    draft.unassignedScores.joinToString().ifEmpty { "—" })
            )
        }

        AbilityScoreMethod.POINT_BUY -> Hint(
            stringResource(
                Res.string.point_buy_hint,
                draft.pointsSpent,
                PlayerCharacterDraft.POINT_BUY_BUDGET
            )
        )
    }

    AbilityName.entries.forEach { ability ->
        val options = when (draft.scoreMethod) {
            AbilityScoreMethod.POINT_BUY -> (PlayerCharacterDraft.POINT_BUY_MIN..PlayerCharacterDraft.POINT_BUY_MAX).toList()
            else -> draft.scorePool.distinct().sortedDescending()
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = ability.label(), color = Theme.colors.textPrimary, modifier = Modifier.weight(1f))
            SingleSelectField(
                value = draft.baseScores[ability],
                onValueChange = { onChange(draft.copy(baseScores = draft.baseScores + (ability to it))) },
                options = options,
                label = stringResource(Res.string.base_score),
                enabled = options.isNotEmpty(),
                modifier = Modifier.width(140.dp),
            )
            Text(text = draft.bonus(ability).takeIf { it > 0 }?.signed().orEmpty(), modifier = Modifier.width(32.dp))
            Text(
                text = draft.score(ability)?.let { "$it (${draft.modifier(ability)?.signed()})" } ?: "—",
                style = Theme.typography.title,
                modifier = Modifier.width(80.dp),
            )
        }
    }

    Section(stringResource(Res.string.background_adjustment))
    Hint(stringResource(Res.string.background_adjustment_hint))
    TabGroup(Modifier.fillMaxWidth()) {
        BackgroundBonus.entries.forEach {
            Tab(
                label = it.label(),
                selected = it == draft.backgroundBonus,
                onClick = { onChange(draft.copy(backgroundBonus = it)) })
        }
    }
    when (draft.backgroundBonus) {
        BackgroundBonus.PLUS_TWO_PLUS_ONE -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SingleSelectField(
                value = draft.plusTwo,
                onValueChange = {
                    onChange(
                        draft.copy(
                            plusTwo = it,
                            plusOne = draft.plusOne.takeIf { one -> one != it })
                    )
                },
                options = AbilityName.entries,
                label = "+2",
                optionLabel = { it.label() },
                required = true,
                modifier = Modifier.weight(1f),
            )
            SingleSelectField(
                value = draft.plusOne,
                onValueChange = { onChange(draft.copy(plusOne = it)) },
                options = AbilityName.entries.filter { it != draft.plusTwo },
                label = "+1",
                optionLabel = { it.label() },
                required = true,
                modifier = Modifier.weight(1f),
            )
        }

        BackgroundBonus.PLUS_ONE_TO_THREE -> MultiSelectField(
            selectedValues = draft.plusOnes,
            onSelectionChange = { if (it.size <= 3) onChange(draft.copy(plusOnes = it)) },
            options = AbilityName.entries,
            label = "+1 / +1 / +1",
            optionLabel = { it.label() },
            required = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Section(stringResource(Res.string.appearance_and_personality))
    StringField(
        value = draft.appearance,
        onValueChange = { onChange(draft.copy(appearance = it)) },
        label = stringResource(Res.string.appearance),
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )
    StringField(
        value = draft.personality,
        onValueChange = { onChange(draft.copy(personality = it)) },
        label = stringResource(Res.string.personality),
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun AlignmentStep(draft: PlayerCharacterDraft, onChange: (PlayerCharacterDraft) -> Unit) {
    Hint(stringResource(Res.string.alignment_hint))
    SingleSelectField(
        value = draft.alignment,
        onValueChange = { onChange(draft.copy(alignment = it)) },
        options = AlignmentName.entries,
        label = stringResource(Res.string.alignment),
        optionLabel = { it.label() },
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DetailsStep(draft: PlayerCharacterDraft, onChange: (PlayerCharacterDraft) -> Unit) {
    StringField(
        value = draft.classFeatures,
        onValueChange = { onChange(draft.copy(classFeatures = it)) },
        label = stringResource(Res.string.class_features),
        placeholder = stringResource(Res.string.class_features_hint),
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )

    Section(stringResource(Res.string.numbers))
    val proficiency = 2
    StatLine(stringResource(Res.string.proficiency_bonus), proficiency.signed())
    StatLine(stringResource(Res.string.hit_points), draft.maxHitPoints?.toString() ?: "—")
    StatLine(stringResource(Res.string.initiative), draft.modifier(AbilityName.DEXTERITY)?.signed() ?: "—")
    StatLine(
        stringResource(Res.string.armor_class_without_armor),
        draft.modifier(AbilityName.DEXTERITY)?.let { (10 + it).toString() } ?: "—")
    val perception =
        draft.modifier(AbilityName.WISDOM)?.let { it + if (SkillName.PERCEPTION in draft.skills) proficiency else 0 }
    StatLine(stringResource(Res.string.passive_perception), perception?.let { (10 + it).toString() } ?: "—")
    StatLine(
        stringResource(Res.string.saving_throws),
        AbilityName.entries.map { ability ->
            val modifier = draft.modifier(ability)?.let { it + if (ability in draft.savingThrows) proficiency else 0 }
            "${ability.label()} ${modifier?.signed() ?: "—"}"
        }.joinToString(),
    )
    StatLine(
        stringResource(Res.string.skill_proficiencies),
        draft.skills.sortedBy { it.ordinal }.map { skill ->
            "${skill.label()} ${draft.modifier(skill.ability)?.let { (it + proficiency).signed() } ?: "—"}"
        }.joinToString().ifEmpty { "—" },
    )

    Section(stringResource(Res.string.final_details))
    StringField(
        value = draft.otherDetails,
        onValueChange = { onChange(draft.copy(otherDetails = it)) },
        label = stringResource(Res.string.other_details),
        placeholder = stringResource(Res.string.other_details_hint),
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun NameStep(
    draft: PlayerCharacterDraft,
    onChange: (PlayerCharacterDraft) -> Unit,
    imageField: @Composable (label: String, modifier: Modifier) -> Unit,
) {
    Section(stringResource(Res.string.summary))
    StatLine(stringResource(Res.string.step_class), "${draft.className} (${draft.hitDie?.label().orEmpty()})")
    StatLine(stringResource(Res.string.species), draft.speciesName)
    StatLine(stringResource(Res.string.background), draft.backgroundName)
    StatLine(stringResource(Res.string.alignment), draft.alignment?.label().orEmpty())
    StatLine(
        stringResource(Res.string.step_ability_scores),
        AbilityName.entries.map { "${it.label()} ${draft.score(it) ?: "—"}" }.joinToString(),
    )
    StatLine(stringResource(Res.string.hit_points), draft.maxHitPoints?.toString() ?: "—")
    StatLine(stringResource(Res.string.languages), draft.languages)

    Section(stringResource(Res.string.step_name))
    imageField(stringResource(Res.string.avatar), Modifier.fillMaxWidth())
    StringField(
        value = draft.name,
        onValueChange = { onChange(draft.copy(name = it)) },
        label = stringResource(Res.string.character_name),
        required = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun Section(title: String) {
    Column(Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = Theme.typography.title)
        HorizontalDivider()
    }
}

@Composable
private fun Hint(text: String) {
    Text(text = text, style = Theme.typography.bodySmall, color = Theme.colors.textSecondary)
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "$label:", color = Theme.colors.textSecondary)
        Text(text = value, color = Theme.colors.textPrimary)
    }
}
