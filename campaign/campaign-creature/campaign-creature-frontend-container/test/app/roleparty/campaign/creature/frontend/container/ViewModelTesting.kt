package app.roleparty.campaign.creature.frontend.container

import app.roleparty.core.frontend.container.ViewModel
import app.roleparty.core.frontend.container.ViewModelEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal fun <E : ViewModelEvent> ViewModel<*, *, E>.recordedEvents(): List<E> {
    val recorded = mutableListOf<E>()
    CoroutineScope(Dispatchers.Main).launch { events.collect { recorded += it } }
    return recorded
}
