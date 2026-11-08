package app.roleparty.campaign.agenda.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class GameSessionId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = GameSessionId(Uuid.generateV7())
        fun from(value: String) = GameSessionId(Uuid.parse(value))
        fun from(value: Uuid) = GameSessionId(value)
    }
}
