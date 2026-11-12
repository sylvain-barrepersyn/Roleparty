package app.roleparty.campaign.tabletop.frontend.container.access

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.contract.web.TabletopEventBody
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import app.roleparty.core.ktor.client.BackendFailure
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TabletopAccessViewModel(private val api: TabletopApi, private val campaignId: CampaignId) :
    ViewModel<TabletopAccessState, TabletopAccessAction, TabletopAccessEvent>(TabletopAccessState.Loading) {

    init {
        watch()
    }

    override fun handle(action: TabletopAccessAction) {
        when (action) {
            TabletopAccessAction.Open -> loaded { copy(opened = gameSession != null) }
            TabletopAccessAction.Close -> loaded { copy(opened = false) }
        }
    }

    private fun watch() {
        viewModelScope.launch {
            while (true) {
                when (val tabletop = api.tabletop(campaignId)) {
                    is Outcome.Failure -> if (tabletop.error is BackendFailure.Rejected) return@launch
                    is Outcome.Success -> {
                        show(tabletop.value)
                        api.events(campaignId).collect(::apply)
                    }
                }
                delay(RETRY_DELAY)
            }
        }
    }

    private fun apply(event: TabletopEventBody) {
        when (event) {
            is TabletopEventBody.Snapshot -> show(event.tabletop)
            is TabletopEventBody.AttendeesChanged -> loaded { copy(attendees = event.attendees) }
            is TabletopEventBody.GameSessionStarted -> {
                loaded { copy(gameSession = event.gameSession) }
                emit(TabletopAccessEvent.GameSessionStarted(event.gameSession))
            }
        }
    }

    private fun show(tabletop: TabletopResponseBody) = update {
        TabletopAccessState.Loaded(tabletop.gameSession, tabletop.attendees, opened = (it as? TabletopAccessState.Loaded)?.opened == true && tabletop.gameSession != null)
    }

    private fun loaded(transform: TabletopAccessState.Loaded.() -> TabletopAccessState.Loaded) =
        update { (it as? TabletopAccessState.Loaded)?.transform() ?: it }

    private companion object {
        val RETRY_DELAY = 3.seconds
    }
}
