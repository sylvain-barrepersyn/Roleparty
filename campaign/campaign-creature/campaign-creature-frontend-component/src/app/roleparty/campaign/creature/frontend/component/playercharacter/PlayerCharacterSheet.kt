package app.roleparty.campaign.creature.frontend.component.playercharacter

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.contract.web.AbilityName
import app.roleparty.campaign.creature.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.creature.contract.web.SkillName
import app.roleparty.campaign.creature.frontend.component.*
import app.roleparty.campaign.creature.frontend.component.playercharacter.creation.label
import app.roleparty.campaign.creature.frontend.component.resources.*
import app.roleparty.core.designsystem.layout.SinglePaneLayout
import app.roleparty.core.designsystem.tab.Tab
import app.roleparty.core.designsystem.tab.TabGroup
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private enum class SheetTab(val label: StringResource) {
    FEATURES_AND_TRAITS(Res.string.features_and_traits),
    INVENTORY(Res.string.inventory),
    BACKGROUND(Res.string.background),
}

@Composable
fun PlayerCharacterSheet(character: PlayerCharacterResponseBody, modifier: Modifier = Modifier) {
    var tab by remember { mutableStateOf(SheetTab.FEATURES_AND_TRAITS) }

    SinglePaneLayout {
        Row(Modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(32.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatBox(
                            stringResource(Res.string.armor_class),
                            character.armorClass.toString(),
                            stringResource(Res.string.unarmored)
                        )
                        StatBox(
                            stringResource(Res.string.initiative),
                            character.initiative.signed(),
                            "(${10 + character.initiative})"
                        )
                        StatBox(
                            stringResource(Res.string.hit_points),
                            character.maxHitPoints.toString(),
                            character.classes.joinToString(" + ") { "${it.level}${it.hitDie.label()}" },
                        )
                    }
                    StatLine(stringResource(Res.string.proficiency_bonus), character.proficiencyBonus.signed())
                    StatLine(stringResource(Res.string.speed), stringResource(Res.string.feet, character.species.speed))
                    StatLine(
                        stringResource(Res.string.senses),
                        listOf(
                            stringResource(Res.string.passive_perception) to character.passivePerception,
                            stringResource(Res.string.passive_investigation) to 10 + character.skill(SkillName.INVESTIGATION),
                            stringResource(Res.string.passive_insight) to 10 + character.skill(SkillName.INSIGHT),
                        ).joinToString { "${it.first} ${it.second}" },
                    )
                    StatLine(
                        stringResource(Res.string.armor_training),
                        character.armorTrainings.sortedBy { it.ordinal }.map { it.label() }.joinToString()
                            .ifEmpty { stringResource(Res.string.none) },
                    )
                    StatLine(
                        stringResource(Res.string.tools),
                        character.background.toolProficiency.ifBlank { stringResource(Res.string.none) })
                    StatLine(
                        stringResource(Res.string.languages),
                        character.languages.joinToString().ifEmpty { stringResource(Res.string.none) })
                }

                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TabGroup(Modifier.fillMaxWidth()) {
                        SheetTab.entries.forEach {
                            Tab(label = stringResource(it.label), selected = it == tab, onClick = { tab = it })
                        }
                    }
                    when (tab) {
                        SheetTab.FEATURES_AND_TRAITS -> {
                            TextBlock(stringResource(Res.string.class_features), character.classFeatures)
                            TextBlock(
                                "${stringResource(Res.string.species_traits)} (${character.species.name})",
                                character.species.traits
                            )
                            TextBlock(stringResource(Res.string.origin_feat), character.background.originFeat)
                        }

                        SheetTab.INVENTORY -> {
                            TextBlock(stringResource(Res.string.equipment), character.equipment)
                            TextBlock(stringResource(Res.string.coins), character.coins.toString())
                        }

                        SheetTab.BACKGROUND -> {
                            StatLine(stringResource(Res.string.background), character.background.name)
                            StatLine(stringResource(Res.string.alignment), character.alignment.label())
                            StatLine(stringResource(Res.string.size), character.species.size.label())
                            TextBlock(stringResource(Res.string.appearance), character.details.appearance)
                            TextBlock(stringResource(Res.string.personality), character.details.personality)
                            TextBlock(stringResource(Res.string.backstory), character.details.backstory)
                            TextBlock(stringResource(Res.string.other_details), character.details.other)
                        }
                    }
                }
            }

            VerticalAbilities(
                AbilityName.entries.map { ability ->
                    AbilityStats(
                        label = ability.label(),
                        score = character.score(ability),
                        savingThrow = character.savingThrow(ability),
                        savingThrowProficient = ability in character.savingThrows,
                        skills = SkillName.entries.filter { it.ability == ability && it in character.skills }
                            .map { it.label() to character.skill(it) },
                    )
                },
                modifier = Modifier.width(220.dp),
            )
        }
    }
}

private fun PlayerCharacterResponseBody.score(ability: AbilityName): Int = when (ability) {
    AbilityName.STRENGTH -> abilityScores.strength
    AbilityName.DEXTERITY -> abilityScores.dexterity
    AbilityName.CONSTITUTION -> abilityScores.constitution
    AbilityName.INTELLIGENCE -> abilityScores.intelligence
    AbilityName.WISDOM -> abilityScores.wisdom
    AbilityName.CHARISMA -> abilityScores.charisma
}

private fun PlayerCharacterResponseBody.savingThrow(ability: AbilityName): Int =
    score(ability).abilityModifier() + if (ability in savingThrows) proficiencyBonus else 0

private fun PlayerCharacterResponseBody.skill(skill: SkillName): Int =
    score(skill.ability).abilityModifier() + if (skill in skills) proficiencyBonus else 0

@Composable
private fun TextBlock(title: String, text: String) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = Theme.typography.title)
        Text(
            text = text.ifBlank { stringResource(Res.string.none) },
            color = if (text.isBlank()) Theme.colors.textSecondary else Theme.colors.textPrimary,
        )
    }
}
