package app.roleparty.security.frontend.component.authentication

sealed interface AuthenticationForm {
    data object SignIn : AuthenticationForm
    data object SignUp : AuthenticationForm
    data class VerifyEmailAddress(val emailAddress: String) : AuthenticationForm
    data object ForgottenPassword : AuthenticationForm
    data class ResetPassword(val emailAddress: String) : AuthenticationForm
}

enum class AuthenticationNotice { EMAIL_VERIFIED, PASSWORD_RESET }

sealed interface AuthenticationFailure {
    data class Rejected(val message: String) : AuthenticationFailure
    data object Unreachable : AuthenticationFailure
    data object Unexpected : AuthenticationFailure
}

interface AuthenticationFormActions {

    fun show(form: AuthenticationForm)

    fun signIn(emailAddress: String, password: String)

    fun signUp(emailAddress: String, password: String)

    fun verifyEmailAddress(emailAddress: String, code: String)

    fun requestPasswordReset(emailAddress: String)

    fun resetPassword(emailAddress: String, code: String, newPassword: String)
}
