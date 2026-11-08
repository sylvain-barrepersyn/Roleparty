package app.roleparty.media.frontend.container.imagefield

import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.media.frontend.component.ImageFieldFailure

sealed interface ImageFieldState : ViewModelState {

    data object Idle : ImageFieldState

    data object Uploading : ImageFieldState

    data class Failed(val failure: ImageFieldFailure) : ImageFieldState
}
