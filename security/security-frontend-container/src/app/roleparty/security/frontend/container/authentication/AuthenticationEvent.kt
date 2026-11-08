package app.roleparty.security.frontend.container.authentication

import app.roleparty.core.frontend.container.ViewModelEvent

sealed interface AuthenticationEvent : ViewModelEvent {

    data object SignedIn : AuthenticationEvent

    data object SignedOut : AuthenticationEvent
}
