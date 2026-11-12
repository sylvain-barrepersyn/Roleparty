package app.roleparty.campaign.tabletop.frontend.container.tabletop

import androidx.compose.ui.graphics.ImageBitmap
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure
import kotlinx.coroutines.flow.StateFlow

sealed interface TabletopState : ViewModelState {

    data object Joining : TabletopState

    data class Joined(
        val self: AccountId,
        val attendees: List<AttendeeBody>,
        val video: (AccountId) -> StateFlow<ImageBitmap?>,
        val camera: Boolean = false,
        val microphone: Boolean = false,
        val cameraUnavailable: Boolean = false,
        val microphoneUnavailable: Boolean = false,
    ) : TabletopState

    data class Failed(val failure: BackendFailure) : TabletopState
}
