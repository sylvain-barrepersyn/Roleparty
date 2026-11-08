package app.roleparty.security.domain.model

sealed interface SecurityError {

    data object InvalidPassword : SecurityError

    data object InvalidCredentials : SecurityError

    data object InvalidRefreshToken : SecurityError

    data object InvalidEmailCode : SecurityError

    data object TooManyAttempts : SecurityError
}
