package app.roleparty.security.domain

import app.roleparty.security.domain.model.PasswordHashingCost
import app.roleparty.security.domain.port.primary.ChangeCredentialEmailAddress
import app.roleparty.security.domain.port.primary.ChangePassword
import app.roleparty.security.domain.port.primary.PurgeExpiredSecrets
import app.roleparty.security.domain.port.primary.RefreshAuthentication
import app.roleparty.security.domain.port.primary.ResetPassword
import app.roleparty.security.domain.port.primary.SignIn
import app.roleparty.security.domain.port.primary.SignOut
import app.roleparty.security.domain.port.primary.SignUp
import app.roleparty.security.domain.port.primary.VerifyEmailAddress
import app.roleparty.security.domain.port.secondary.Accounts
import app.roleparty.security.domain.port.secondary.Credentials
import app.roleparty.security.domain.port.secondary.EmailCodes
import app.roleparty.security.domain.port.secondary.RefreshTokens
import app.roleparty.security.domain.port.secondary.SecurityEmails
import app.roleparty.security.domain.service.AccessTokenIssuingService
import app.roleparty.security.domain.service.AttemptLimitingService
import app.roleparty.security.domain.service.AuthenticationIssuingService
import app.roleparty.security.domain.service.AuthenticationRefreshingService
import app.roleparty.security.domain.service.CredentialEmailAddressChangingService
import app.roleparty.security.domain.service.EmailAddressVerifyingService
import app.roleparty.security.domain.service.EmailCodeVerifyingService
import app.roleparty.security.domain.service.EmailThrottlingService
import app.roleparty.security.domain.service.ExpiredSecretsPurgingService
import app.roleparty.security.domain.service.FAILED_ATTEMPTS_WINDOW
import app.roleparty.security.domain.service.MAX_FAILED_ATTEMPTS
import app.roleparty.security.domain.service.PasswordChangingService
import app.roleparty.security.domain.service.PasswordHashingService
import app.roleparty.security.domain.service.PasswordReplacingService
import app.roleparty.security.domain.service.PasswordResettingService
import app.roleparty.security.domain.service.SigningInService
import app.roleparty.security.domain.service.SigningOutService
import app.roleparty.security.domain.service.SigningUpService
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import kotlin.time.Clock
import kotlinx.coroutines.runBlocking

private const val DUMMY_PASSWORD = "roleparty-timing-equalization-password"

class SecurityModule(
    accounts: Accounts,
    credentials: Credentials,
    refreshTokens: RefreshTokens,
    emailCodes: EmailCodes,
    emails: SecurityEmails,
    accessTokenPublicKey: ECPublicKey,
    accessTokenPrivateKey: ECPrivateKey,
    passwordHashingCost: PasswordHashingCost = PasswordHashingCost(),
    clock: Clock = Clock.System,
) {
    private val passwordHashingService = PasswordHashingService(passwordHashingCost)
    private val dummyPasswordHash = runBlocking { passwordHashingService.hash(DUMMY_PASSWORD) }
    private val accessTokenIssuingService = AccessTokenIssuingService(accessTokenPublicKey, accessTokenPrivateKey, clock)
    private val failedAttempts = AttemptLimitingService(MAX_FAILED_ATTEMPTS, FAILED_ATTEMPTS_WINDOW, clock)
    private val emailThrottlingService = EmailThrottlingService(clock)
    private val emailCodeVerifyingService = EmailCodeVerifyingService(emailCodes, failedAttempts, clock)
    private val authenticationIssuingService = AuthenticationIssuingService(refreshTokens, accessTokenIssuingService, clock)
    private val passwordReplacingService = PasswordReplacingService(credentials, passwordHashingService, refreshTokens)

    val signUp: SignUp = SigningUpService(credentials, passwordHashingService, emails, emailThrottlingService, emailCodeVerifyingService)

    val verifyEmailAddress: VerifyEmailAddress = EmailAddressVerifyingService(accounts, credentials, emailCodeVerifyingService)

    val signIn: SignIn = SigningInService(credentials, passwordHashingService, failedAttempts, authenticationIssuingService, dummyPasswordHash)

    val refreshAuthentication: RefreshAuthentication = AuthenticationRefreshingService(refreshTokens, authenticationIssuingService, clock)

    val signOut: SignOut = SigningOutService(refreshTokens)

    val resetPassword: ResetPassword =
        PasswordResettingService(credentials, emails, emailThrottlingService, emailCodeVerifyingService, passwordReplacingService, failedAttempts)

    val changePassword: ChangePassword = PasswordChangingService(credentials, passwordHashingService, failedAttempts, passwordReplacingService)

    val changeCredentialEmailAddress: ChangeCredentialEmailAddress = CredentialEmailAddressChangingService(credentials)

    val purgeExpiredSecrets: PurgeExpiredSecrets = ExpiredSecretsPurgingService(refreshTokens, emailCodes, clock)
}
