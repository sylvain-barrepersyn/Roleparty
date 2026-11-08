package app.roleparty.security.infrastructure

import app.roleparty.communication.commons.Email
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredSecurityEmails
import app.roleparty.security.test.SecurityEmailsContractSubject
import app.roleparty.security.test.SecurityEmailsContractTest
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.slf4j.LoggerFactory

class InfrastructuredSecurityEmailsContractTest : SecurityEmailsContractTest({
    val sent = CopyOnWriteArrayList<Email>()
    val emails = InfrastructuredSecurityEmails(
        scope = CoroutineScope(Dispatchers.Default),
        sender = { sent += it },
        log = LoggerFactory.getLogger("test"),
    )
    SecurityEmailsContractSubject(emails) { to -> sent.filter { it.to == to }.map { it.text } }
})
