package app.roleparty.security.frontend.container.authentication

import app.roleparty.core.frontend.container.ViewModelAction
import app.roleparty.security.frontend.component.authentication.AuthenticationForm

sealed interface AuthenticationAction : ViewModelAction {

    data class Show(val form: AuthenticationForm) : AuthenticationAction

    data class SignIn(val emailAddress: String, val password: String) : AuthenticationAction

    data class SignUp(val emailAddress: String, val password: String) : AuthenticationAction

    data class VerifyEmailAddress(val emailAddress: String, val code: String) : AuthenticationAction

    data class RequestPasswordReset(val emailAddress: String) : AuthenticationAction

    data class ResetPassword(val emailAddress: String, val code: String, val newPassword: String) : AuthenticationAction

    data object SignOut : AuthenticationAction
}
