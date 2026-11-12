package app.roleparty.campaign.tabletop.frontend.container.tabletop

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.frontend.container.TabletopParticipation
import app.roleparty.campaign.tabletop.frontend.container.data.TableStatus
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopCall
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopCalls
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TabletopViewModel(
    private val calls: TabletopCalls,
    private val campaignId: CampaignId,
    private val participation: TabletopParticipation,
) : ViewModel<TabletopState, TabletopAction, TabletopEvent>(TabletopState.Joining) {

    private var call: TabletopCall? = null
    private var watching: Job? = null
    private var camera = false
    private var microphone = false

    override fun handle(action: TabletopAction) {
        when (action) {
            TabletopAction.Join -> join()
            TabletopAction.Leave -> leave()
            TabletopAction.Close -> {
                leave()
                emit(TabletopEvent.Closed)
            }

            TabletopAction.Retry -> {
                leave()
                join()
            }

            is TabletopAction.TurnCamera -> turnCamera(action.on)
            is TabletopAction.TurnMicrophone -> turnMicrophone(action.on)
        }
    }

    override fun onCleared() {
        call?.leave()
    }

    private fun join() {
        if (call != null) return
        val joined = calls.join(campaignId, participation.playerCharacterId).also { call = it }
        watching = viewModelScope.launch {
            joined.status.collect { status ->
                update { current ->
                    when (status) {
                        TableStatus.Joining -> TabletopState.Joining
                        is TableStatus.Refused -> TabletopState.Failed(status.failure)
                        is TableStatus.Joined ->
                            (current as? TabletopState.Joined)?.copy(self = status.self, attendees = status.attendees)
                                ?: TabletopState.Joined(status.self, status.attendees, joined::video, camera, microphone)
                    }
                }
            }
        }
    }

    private fun leave() {
        watching?.cancel()
        call?.leave()
        call = null
        camera = false
        microphone = false
        update { TabletopState.Joining }
    }

    private fun turnCamera(on: Boolean) {
        val joined = call ?: return
        viewModelScope.launch {
            val turned = joined.turnCamera(on)
            if (call !== joined) return@launch
            if (turned) camera = on
            update { (it as? TabletopState.Joined)?.copy(camera = camera, cameraUnavailable = !turned) ?: it }
        }
    }

    private fun turnMicrophone(on: Boolean) {
        val joined = call ?: return
        viewModelScope.launch {
            val turned = joined.turnMicrophone(on)
            if (call !== joined) return@launch
            if (turned) microphone = on
            update { (it as? TabletopState.Joined)?.copy(microphone = microphone, microphoneUnavailable = !turned) ?: it }
        }
    }
}
