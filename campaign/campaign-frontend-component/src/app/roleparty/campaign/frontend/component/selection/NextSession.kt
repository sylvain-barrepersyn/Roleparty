package app.roleparty.campaign.frontend.component.selection

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

internal sealed interface NextSession {

    data object Today : NextSession

    data class InDays(val days: Int) : NextSession

    data class On(val date: LocalDate) : NextSession

    data object None : NextSession

    companion object {
        fun of(date: LocalDate?, today: LocalDate): NextSession {
            val days = date?.let { today.daysUntil(it) }
            return when {
                date == null || days == null || days < 0 -> None
                days == 0 -> Today
                days <= 7 -> InDays(days)
                else -> On(date)
            }
        }
    }
}
