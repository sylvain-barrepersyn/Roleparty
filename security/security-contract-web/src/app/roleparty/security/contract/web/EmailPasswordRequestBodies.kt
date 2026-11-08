package app.roleparty.security.contract.web

import kotlinx.serialization.Serializable

@Serializable
data class EmailPasswordSignInRequestBody(val emailAddress: String, val password: String)

@Serializable
data class EmailPasswordSignUpRequestBody(val emailAddress: String, val password: String)

@Serializable
data class EmailVerificationRequestBody(val emailAddress: String, val code: String)

@Serializable
data class PasswordResetRequestBody(val emailAddress: String)

@Serializable
data class PasswordResetConfirmationRequestBody(val emailAddress: String, val code: String, val newPassword: String)

@Serializable
data class PasswordChangeRequestBody(val currentPassword: String, val newPassword: String)
