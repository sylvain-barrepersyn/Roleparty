package app.roleparty.communication.commons

sealed interface CommunicationError {

    data class InvalidEmailAddress(val value: String) : CommunicationError
}
