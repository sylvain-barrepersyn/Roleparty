package app.roleparty.security.frontend.container

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import app.roleparty.core.designsystem.surface.BaseSurface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.RolepartyTheme
import app.roleparty.security.frontend.component.authentication.SignOutButton
import app.roleparty.security.frontend.container.authentication.AuthenticationContainer
import app.roleparty.security.frontend.container.authentication.AuthenticationViewModel
import app.roleparty.security.frontend.container.data.SecurityApi
import app.roleparty.security.frontend.container.data.SessionStore
import app.roleparty.security.frontend.container.data.SecurityFailure
import app.roleparty.security.frontend.container.data.StoredSession
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.Locale
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class AuthenticationContainerTest : FunSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val api = FakeSecurityApi()
    private val store = InMemorySessionStore()
    private val defaultLocale = Locale.getDefault()

    private val email = "gimli@roleparty.app"
    private val password = "a-valid-password"

    private val securityModule = module {
        single<SecurityApi> { api }
        single<SessionStore> { store }
        viewModelOf(::AuthenticationViewModel)
    }

    private fun ComposeUiTest.gate() {
        setContent {
            KoinIsolatedContext(koinApplication { modules(securityModule) }) {
                RolepartyTheme {
                    BaseSurface {
                        AuthenticationContainer { signOut ->
                            Text("Signed in content")
                            SignOutButton(onClick = signOut)
                        }.Render()
                    }
                }
            }
        }
        waitForIdle()
    }

    private fun ComposeUiTest.type(index: Int, text: String) {
        onAllNodes(hasSetTextAction())[index].performTextInput(text)
    }

    private fun ComposeUiTest.click(text: String) {
        onNodeWithText(text).performClick()
        waitForIdle()
    }

    private fun ComposeUiTest.eventuallyShows(text: String) {
        waitUntil(timeoutMillis = TIMEOUT) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun ComposeUiTest.shows(text: String) {
        eventuallyShows(text)
        onNodeWithText(text).assertExists()
    }

    private fun ComposeUiTest.hides(text: String) {
        withClue("`$text` should not be displayed") { onAllNodesWithText(text).fetchSemanticsNodes().shouldBeEmpty() }
    }

    init {
        beforeTest { Locale.setDefault(Locale.ENGLISH) }
        afterTest { Locale.setDefault(defaultLocale) }

        test("should sign in, store the session and show the protected content") {
            runComposeUiTest {
                // Given
                gate()

                // When
                type(0, email)
                type(1, password)
                click("Sign in")

                // Then
                shows("Signed in content")
                api.calls shouldBe listOf("signIn:$email:$password")
                store.session shouldBe api.session
            }
        }

        test("should submit with the enter key") {
            runComposeUiTest {
                // Given
                gate()
                type(0, email)
                type(1, password)

                // When
                onAllNodes(hasSetTextAction())[1].requestFocus()
                onAllNodes(hasSetTextAction())[1].performKeyInput { pressKey(Key.Enter) }
                waitForIdle()

                // Then
                shows("Signed in content")
            }
        }

        test("should not show errors before the first submit, and block an invalid form") {
            runComposeUiTest {
                // Given
                gate()

                // Then
                hides("Enter a valid email address.")

                // When
                type(0, "not-an-email")
                hides("Enter a valid email address.")
                click("Sign in")

                // Then
                shows("Enter a valid email address.")
                shows("This field is required.")
                api.calls shouldBe emptyList<String>()
            }
        }

        test("should show the message of the backend when it refuses") {
            runComposeUiTest {
                // Given
                api.failure = SecurityFailure.Rejected("security.invalid-credentials", "The password is incorrect.")
                gate()

                // When
                type(0, email)
                type(1, password)
                click("Sign in")

                // Then
                shows("The password is incorrect.")
                hides("Signed in content")
                store.session.shouldBeNull()
            }
        }

        test("should explain that the backend is unreachable") {
            runComposeUiTest {
                // Given
                api.failure = SecurityFailure.Unreachable
                gate()

                // When
                type(0, email)
                type(1, password)
                click("Sign in")

                // Then
                shows("Roleparty’s servers can’t be reached. Check your connection and try again.")
            }
        }

        test("should go through sign up then verification and come back to sign in with the address") {
            runComposeUiTest {
                // Given
                gate()
                click("Create an account")

                // When
                type(0, email)
                type(1, password)
                type(2, password)
                onAllNodesWithText("Create an account")[0].performClick()
                waitForIdle()

                // Then
                shows("If this address is available, we sent you a code. Enter it to confirm your address.")

                // When
                type(0, "ABCD2345")
                click("Confirm")

                // Then
                shows("Your address is confirmed. You can sign in.")
                shows(email)
                api.calls shouldBe listOf("signUp:$email:$password", "verify:$email:ABCD2345")
            }
        }

        test("should block a sign up with different passwords or a short one") {
            runComposeUiTest {
                // Given
                gate()
                click("Create an account")

                // When
                type(0, email)
                type(1, "short")
                type(2, "other")
                click("Create an account")

                // Then
                shows("The password must contain between 8 and 128 characters.")
                shows("The two passwords are different.")
                api.calls shouldBe emptyList<String>()
            }
        }

        test("should reset a forgotten password") {
            runComposeUiTest {
                // Given
                gate()
                click("Forgot your password?")

                // When
                type(0, email)
                click("Send me a code")

                // Then
                shows("If this address has an account, we sent you a code. Enter it to choose a new password.")

                // When
                type(0, "ABCD2345")
                type(1, password)
                type(2, password)
                click("Confirm")

                // Then
                shows("Your password has changed. You can sign in.")
                api.calls shouldBe listOf("requestReset:$email", "reset:$email:ABCD2345:$password")
            }
        }

        test("should restore a stored session on start") {
            runComposeUiTest {
                // Given
                store.session = StoredSession("old-access", "old-refresh")

                // When
                gate()

                // Then
                shows("Signed in content")
                api.calls shouldBe listOf("refresh:old-refresh")
                store.session shouldBe api.session
            }
        }

        test("should forget a stored session the backend refuses") {
            runComposeUiTest {
                // Given
                store.session = StoredSession("old-access", "old-refresh")
                api.failure = SecurityFailure.Rejected("security.invalid-refresh-token", "Your session is no longer valid.")

                // When
                gate()

                // Then
                shows("Sign in")
                store.session.shouldBeNull()
            }
        }

        test("should keep a stored session when the backend is unreachable") {
            runComposeUiTest {
                // Given
                val session = StoredSession("old-access", "old-refresh")
                store.session = session
                api.failure = SecurityFailure.Unreachable

                // When
                gate()

                // Then
                shows("Roleparty’s servers can’t be reached. Check your connection and try again.")
                store.session shouldBe session
            }
        }

        test("should sign out and forget the session") {
            runComposeUiTest {
                // Given
                store.session = StoredSession("old-access", "old-refresh")
                gate()

                // When
                click("Sign out")

                // Then
                shows("Sign in")
                store.session.shouldBeNull()
                api.calls.last() shouldBe "signOut:refresh"
            }
        }

        test("should sign out locally even when the backend is unreachable") {
            runComposeUiTest {
                // Given
                store.session = StoredSession("old-access", "old-refresh")
                gate()
                api.failure = SecurityFailure.Unreachable

                // When
                click("Sign out")

                // Then
                shows("Sign in")
                store.session.shouldBeNull()
            }
        }
    }
}

private const val TIMEOUT = 5_000L
