package app.roleparty.campaign.creature.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PlayerCharacterId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = PlayerCharacterId(Uuid.generateV7())
        fun from(value: String) = PlayerCharacterId(Uuid.parse(value))
        fun from(value: Uuid) = PlayerCharacterId(value)
    }
}
