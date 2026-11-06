package app.roleparty.core.designsystem.field.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import java.text.Collator

internal data class LabeledOption<T>(val value: T, val label: String)

@Composable
internal fun <T> labeledOptions(
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    sortByLabel: Boolean,
): List<LabeledOption<T>> {
    val labeled = options.map { option ->
        key(option) { LabeledOption(option, optionLabel(option)) }
    }
    return if (sortByLabel) {
        labeled.sortedWith(compareBy(Collator.getInstance()) { it.label })
    } else {
        labeled
    }
}
