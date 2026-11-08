package app.roleparty.security.test

class InMemorySecurityEmailsContractTest : SecurityEmailsContractTest({
    val emails = InMemorySecurityEmails()
    SecurityEmailsContractSubject(emails) { to ->
        emails.sent.filter { it.to == to }.map {
            when (it) {
                is SentEmail.Verification -> it.code
                is SentEmail.PasswordReset -> it.code
                is SentEmail.AlreadyRegistered -> ""
            }
        }
    }
})
