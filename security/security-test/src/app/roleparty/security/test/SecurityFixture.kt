package app.roleparty.security.test

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.ControlledClock
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.SecurityModule
import app.roleparty.security.domain.model.Credential
import app.roleparty.security.domain.model.PasswordHashingCost
import app.roleparty.security.domain.model.RefreshToken
import app.roleparty.security.domain.model.StoredRefreshToken
import io.kotest.matchers.nulls.shouldNotBeNull
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.util.Locale

class SecurityFixture(clock: ControlledClock = ControlledClock()) {

    private val accounts = InMemoryAccounts()
    private val credentials = InMemoryCredentials()
    private val refreshTokens = InMemoryRefreshTokens()
    private val emailCodes = InMemoryEmailCodes()
    private val emails = InMemorySecurityEmails()
    private val accessTokenKeys = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()

    val accessTokenPublicKey = accessTokenKeys.public as ECPublicKey

    private val module = SecurityModule(
        accounts = accounts,
        credentials = credentials,
        refreshTokens = refreshTokens,
        emailCodes = emailCodes,
        emails = emails,
        accessTokenPublicKey = accessTokenPublicKey,
        accessTokenPrivateKey = accessTokenKeys.private as ECPrivateKey,
        passwordHashingCost = PasswordHashingCost.MINIMAL,
        clock = clock,
    )

    val signUp = module.signUp
    val verifyEmailAddress = module.verifyEmailAddress
    val signIn = module.signIn
    val refreshAuthentication = module.refreshAuthentication
    val signOut = module.signOut
    val resetPassword = module.resetPassword
    val changePassword = module.changePassword
    val changeCredentialEmailAddress = module.changeCredentialEmailAddress
    val purgeExpiredSecrets = module.purgeExpiredSecrets

    suspend fun `given a verified user`(emailAddress: EmailAddress, password: String): AccountId {
        signUp(emailAddress, password, Locale.ENGLISH).shouldBeSuccess()
        verifyEmailAddress(emailAddress, lastCodeSentTo(emailAddress)).shouldBeSuccess()
        return accountOf(emailAddress)
    }

    suspend fun `given a signed in user`(emailAddress: EmailAddress, password: String): RefreshToken {
        `given a verified user`(emailAddress, password)
        return `given a new session`(emailAddress, password)
    }

    suspend fun `given a new session`(emailAddress: EmailAddress, password: String): RefreshToken =
        signIn(emailAddress, password).shouldBeSuccess().refreshToken.value

    fun emailsSentTo(emailAddress: EmailAddress): List<SentEmail> = emails.sent.filter { it.to == emailAddress }

    fun lastCodeSentTo(emailAddress: EmailAddress): String =
        when (val email = emailsSentTo(emailAddress).last()) {
            is SentEmail.Verification -> email.code
            is SentEmail.PasswordReset -> email.code
            is SentEmail.AlreadyRegistered -> error("This email holds no code")
        }

    suspend fun credentialOf(emailAddress: EmailAddress): Credential? = credentials.findByEmailAddress(emailAddress)

    suspend fun accountOf(emailAddress: EmailAddress): AccountId = credentialOf(emailAddress).shouldNotBeNull().accountId

    fun registeredAccounts(): Int = accounts.count()

    fun refreshTokens(): List<StoredRefreshToken> = refreshTokens.all()

    fun storedEmailCodes(): Int = emailCodes.count()
}
