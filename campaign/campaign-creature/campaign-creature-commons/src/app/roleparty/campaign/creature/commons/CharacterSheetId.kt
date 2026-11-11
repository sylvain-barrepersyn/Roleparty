package app.roleparty.campaign.creature.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class CharacterSheetId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = CharacterSheetId(Uuid.generateV7())
        fun from(value: String) = CharacterSheetId(Uuid.parse(value))
        fun from(value: Uuid) = CharacterSheetId(value)
    }
}
