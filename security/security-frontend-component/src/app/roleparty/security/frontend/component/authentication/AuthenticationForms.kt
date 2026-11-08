package app.roleparty.security.frontend.component.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.security.frontend.component.authentication.AuthenticationNotice.EMAIL_VERIFIED
import app.roleparty.security.frontend.component.authentication.AuthenticationNotice.PASSWORD_RESET
import app.roleparty.security.frontend.component.resources.Res
import app.roleparty.security.frontend.component.resources.back
import app.roleparty.security.frontend.component.resources.code
import app.roleparty.security.frontend.component.resources.confirm
import app.roleparty.security.frontend.component.resources.email_address
import app.roleparty.security.frontend.component.resources.email_verified
import app.roleparty.security.frontend.component.resources.forgotten_password
import app.roleparty.security.frontend.component.resources.new_password
import app.roleparty.security.frontend.component.resources.password
import app.roleparty.security.frontend.component.resources.password_confirmation
import app.roleparty.security.frontend.component.resources.password_reset
import app.roleparty.security.frontend.component.resources.reset_instructions
import app.roleparty.security.frontend.component.resources.send_code
import app.roleparty.security.frontend.component.resources.sign_in
import app.roleparty.security.frontend.component.resources.sign_up
import app.roleparty.security.frontend.component.resources.unexpected_failure
import app.roleparty.security.frontend.component.resources.unreachable
import app.roleparty.security.frontend.component.resources.verify_instructions
import app.roleparty.security.frontend.component.resources.welcome
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val FORM_WIDTH = 400.dp

@Composable
fun AuthenticationForms(
    form: AuthenticationForm,
    knownEmailAddress: String,
    loading: Boolean,
    notice: AuthenticationNotice?,
    failure: AuthenticationFailure?,
    actions: AuthenticationFormActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(FORM_WIDTH),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(vector = IconVector.ROLEPARTY, tint = Theme.colors.primary, size = 64.dp)
        Text(stringResource(Res.string.welcome), style = Theme.typography.headline)

        notice?.let { Text(stringResource(it.message()), color = Theme.colors.textPrimary) }
        failure?.let { Text(it.message(), color = Theme.colors.error) }

        when (form) {
            AuthenticationForm.SignIn -> SignInForm(knownEmailAddress, loading, actions)
            AuthenticationForm.SignUp -> SignUpForm(knownEmailAddress, loading, actions)
            is AuthenticationForm.VerifyEmailAddress -> VerifyEmailAddressForm(form.emailAddress, loading, actions)
            AuthenticationForm.ForgottenPassword -> ForgottenPasswordForm(knownEmailAddress, loading, actions)
            is AuthenticationForm.ResetPassword -> ResetPasswordForm(form.emailAddress, loading, actions)
        }
    }
}

@Composable
private fun SignInForm(knownEmailAddress: String, loading: Boolean, actions: AuthenticationFormActions) {
    var emailAddress by remember { mutableStateOf(knownEmailAddress) }
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val emailAddressError = emailAddressError(emailAddress)
    val passwordError = requiredError(password)
    val submit = {
        submitted = true
        if (emailAddressError == null && passwordError == null) actions.signIn(emailAddress, password)
    }

    FormFields {
        FormField(Res.string.email_address, emailAddress, { emailAddress = it }, emailAddressError.takeIf { submitted }, loading, submit)
        FormField(Res.string.password, password, { password = it }, passwordError.takeIf { submitted }, loading, submit, secret = true)
        Button(leadingIcon = IconVector.ACCOUNT, text = stringResource(Res.string.sign_in), onClick = submit, modifier = Modifier.fillMaxWidth(), enabled = !loading)
        SecondaryActions {
            Button(leadingIcon = IconVector.ADD, text = stringResource(Res.string.sign_up), onClick = { actions.show(AuthenticationForm.SignUp) }, enabled = !loading)
            Button(leadingIcon = IconVector.REFRESH, text = stringResource(Res.string.forgotten_password), onClick = { actions.show(AuthenticationForm.ForgottenPassword) }, enabled = !loading)
        }
    }
}

@Composable
private fun SignUpForm(knownEmailAddress: String, loading: Boolean, actions: AuthenticationFormActions) {
    var emailAddress by remember { mutableStateOf(knownEmailAddress) }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val emailAddressError = emailAddressError(emailAddress)
    val passwordError = newPasswordError(password)
    val confirmationError = confirmationError(password, confirmation)
    val submit = {
        submitted = true
        if (emailAddressError == null && passwordError == null && confirmationError == null) actions.signUp(emailAddress, password)
    }

    FormFields {
        FormField(Res.string.email_address, emailAddress, { emailAddress = it }, emailAddressError.takeIf { submitted }, loading, submit)
        FormField(Res.string.password, password, { password = it }, passwordError.takeIf { submitted }, loading, submit, secret = true)
        FormField(Res.string.password_confirmation, confirmation, { confirmation = it }, confirmationError.takeIf { submitted }, loading, submit, secret = true)
        Button(leadingIcon = IconVector.ADD, text = stringResource(Res.string.sign_up), onClick = submit, modifier = Modifier.fillMaxWidth(), enabled = !loading)
        SecondaryActions {
            Button(leadingIcon = IconVector.PREVIOUS, text = stringResource(Res.string.back), onClick = { actions.show(AuthenticationForm.SignIn) }, enabled = !loading)
        }
    }
}

@Composable
private fun VerifyEmailAddressForm(emailAddress: String, loading: Boolean, actions: AuthenticationFormActions) {
    var code by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val codeError = requiredError(code)
    val submit = {
        submitted = true
        if (codeError == null) actions.verifyEmailAddress(emailAddress, code)
    }

    FormFields {
        Text(stringResource(Res.string.verify_instructions))
        FormField(Res.string.code, code, { code = it }, codeError.takeIf { submitted }, loading, submit)
        Button(leadingIcon = IconVector.CONFIRM, text = stringResource(Res.string.confirm), onClick = submit, modifier = Modifier.fillMaxWidth(), enabled = !loading)
        SecondaryActions {
            Button(leadingIcon = IconVector.PREVIOUS, text = stringResource(Res.string.back), onClick = { actions.show(AuthenticationForm.SignUp) }, enabled = !loading)
        }
    }
}

@Composable
private fun ForgottenPasswordForm(knownEmailAddress: String, loading: Boolean, actions: AuthenticationFormActions) {
    var emailAddress by remember { mutableStateOf(knownEmailAddress) }
    var submitted by remember { mutableStateOf(false) }

    val emailAddressError = emailAddressError(emailAddress)
    val submit = {
        submitted = true
        if (emailAddressError == null) actions.requestPasswordReset(emailAddress)
    }

    FormFields {
        FormField(Res.string.email_address, emailAddress, { emailAddress = it }, emailAddressError.takeIf { submitted }, loading, submit)
        Button(leadingIcon = IconVector.AT, text = stringResource(Res.string.send_code), onClick = submit, modifier = Modifier.fillMaxWidth(), enabled = !loading)
        SecondaryActions {
            Button(leadingIcon = IconVector.PREVIOUS, text = stringResource(Res.string.back), onClick = { actions.show(AuthenticationForm.SignIn) }, enabled = !loading)
        }
    }
}

@Composable
private fun ResetPasswordForm(emailAddress: String, loading: Boolean, actions: AuthenticationFormActions) {
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val codeError = requiredError(code)
    val passwordError = newPasswordError(password)
    val confirmationError = confirmationError(password, confirmation)
    val submit = {
        submitted = true
        if (codeError == null && passwordError == null && confirmationError == null) actions.resetPassword(emailAddress, code, password)
    }

    FormFields {
        Text(stringResource(Res.string.reset_instructions))
        FormField(Res.string.code, code, { code = it }, codeError.takeIf { submitted }, loading, submit)
        FormField(Res.string.new_password, password, { password = it }, passwordError.takeIf { submitted }, loading, submit, secret = true)
        FormField(Res.string.password_confirmation, confirmation, { confirmation = it }, confirmationError.takeIf { submitted }, loading, submit, secret = true)
        Button(leadingIcon = IconVector.CONFIRM, text = stringResource(Res.string.confirm), onClick = submit, modifier = Modifier.fillMaxWidth(), enabled = !loading)
        SecondaryActions {
            Button(leadingIcon = IconVector.PREVIOUS, text = stringResource(Res.string.back), onClick = { actions.show(AuthenticationForm.ForgottenPassword) }, enabled = !loading)
        }
    }
}

@Composable
private fun FormFields(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
}

@Composable
private fun SecondaryActions(content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { content() }
}

@Composable
private fun FormField(
    label: StringResource,
    value: String,
    onValueChange: (String) -> Unit,
    error: StringResource?,
    loading: Boolean,
    submit: () -> Unit,
    secret: Boolean = false,
) {
    StringField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(label),
        modifier = Modifier.fillMaxWidth(),
        enabled = !loading,
        error = error?.let { stringResource(it) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { submit() }),
        visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None,
    )
}

private fun AuthenticationNotice.message() = when (this) {
    EMAIL_VERIFIED -> Res.string.email_verified
    PASSWORD_RESET -> Res.string.password_reset
}

@Composable
private fun AuthenticationFailure.message(): String = when (this) {
    is AuthenticationFailure.Rejected -> message
    AuthenticationFailure.Unreachable -> stringResource(Res.string.unreachable)
    AuthenticationFailure.Unexpected -> stringResource(Res.string.unexpected_failure)
}
