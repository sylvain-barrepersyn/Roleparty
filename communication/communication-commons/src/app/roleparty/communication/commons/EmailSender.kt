package app.roleparty.communication.commons

data class Email(
    val to: EmailAddress,
    val subject: String,
    val text: String,
    val html: String,
)

fun interface EmailSender {

    suspend fun send(email: Email)
}
